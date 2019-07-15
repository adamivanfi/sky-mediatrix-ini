/**
 * Sky channel based routing model
 */
package com.nttdata.de.ityx.mediatrix.routing;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.routing.RoutingModel;
import de.ityx.mediatrix.data.Operator;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.server.connect.NoQuestionsWaitingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SkyChannelRoutingModel implements RoutingModel {

    private static final long serialVersionUID = 6188022316609968454L;
    public static final String CHANNEL_BASED_ROUTING = "Channel priorized agency routing (NTT)";
    public static final int ROUTINGID = 20;

     //skill_factor, frage.prioritaet/5 - rausgenommen
    private static final String GETNEXTQUESTION_SQL = "select frageid,"
            + " globalsla+  globalsla *(case when o.channel='EMAIL' then channelMail_factor else channelDoc_factor end) rank "
  //14.01.2015 (R.Koch/Meinusch) - initial wird der Faktor der TP-Prio auf 0* gesetzt 
  //        + " globalsla+   globalsla *(case when o.channel='EMAIL' then channelMail_factor else channelDoc_factor end)*            ((maxtpprio-tpprio+1)/maxtpprio) rank " 
  //14.01.2015 - Zuständigkeit (SkillFactor) und Priorität der Frage soll nicht berücksichtigt werden
  //        + " globalsla+ globalsla *(case when o.channel='EMAIL' then channelMail_factor else channelDoc_factor end)+ 0.1* globalsla*((maxtpprio-tpprio+1)/maxtpprio) + 0.15*(frage.prioritaet/5) +0.15*skill_factor   rank "           
            + "from "
            + "(select frageid, teilprojektid, channel, sysdate - nvl(GLOBAL_inslauntil,STARTED) globalsla "
            + " from EBMF_FRAGEMETA "
            + " where ENDED is null "
  // 14.01.2015 (R.Koch) - Es sollen Fragen beliebiger Agentur gepusht werden
            //+ "   and ASSIGNED_AGENTURID=(select agentur from  ar_mitarbeiter where ar_mitarbeiter.MXID=?)" //1-Mitarbeiterid
            + "   and ASSIGNED_AGENTURID>0"
            + ") o, "
            + "(select  z.mitarbeiterid, teilprojektid, z.prozent/100 skill_factor, tp.PRIO tpprio, "
            + "         (select min(case  when min(liegezeit)<=0 then 1 else min(liegezeit) end ) minliegezeit from AR_AGENTUR_LIEGEZEITEN where agentur=am.agentur group by agentur) / (select max(liegezeit) from AR_AGENTUR_LIEGEZEITEN where typ =0 and agentur=am.agentur) channelMail_factor, "
            + "         (select min(case  when min(liegezeit)<=0 then 1 else min(liegezeit) end ) minliegezeit from AR_AGENTUR_LIEGEZEITEN where agentur=am.agentur group by agentur) / (select max(liegezeit) from AR_AGENTUR_LIEGEZEITEN where typ <>0 and agentur=am.agentur)    channelDoc_factor "
            + "  from ZUSTAENDIGKEIT z, ar_mitarbeiter am,  teilprojekt tp "
            + "  where z.mitarbeiterid=? and am.mxid=z.mitarbeiterid and tp.id=z.TEILPROJEKTID and tp.geloeschtam=0 and z.prozent>0  " //2-Mitarbeiterid
            + "       and not exists (select * from ar_agentur_blacklist b where b.agentur=am.AGENTUR and tp.id=b.teilprojekt)" 
            + "       and     exists (select * from rechte r where z.mitarbeiterid=r.mitarbeiterid and r.bezeichnung= 'operatorrecht' and r.parameter=tp.id and r.parameter=tp.id  ) ) q,"
            + "  frage , (select max(prio) maxtpprio from teilprojekt where geloeschtam=0  ) mtp  "
            + "where q.teilprojektid=o.teilprojektid "
            + "  and frage.id=o.frageid and frage.gesperrtvon=0"
            + "  and frage.TEILPROJEKTID>0 " //vom maildeamon gesperrt
            + "  and %s " //responsibility
            + "      frage.GELOESCHTAM=0 and frage.status not in ('erledigt', 'extern-weitergeleitet', 'wiedervorlage', 'blockiert' ) "
            + "  order by rank desc, frageid asc";

    
    private static final String GETNEXTWVQUESTION_SQL = "select frageid "
  // 16.01.2015 (R.Koch) - Sortierung nach frageid (indirekt nach alter) wurde gewünscht von Sky
  //          + "(sysdate-ityxtimetodate(frage.wiedervorlagezeit)) * globalsla+   globalsla *(case when o.channel='EMAIL' then channelMail_factor else channelDoc_factor end)  rank "
  // s. kommentare oben
  //          + "(sysdate-ityxtimetodate(frage.wiedervorlagezeit)) * globalsla + globalsla *(case when o.channel='EMAIL' then channelMail_factor else channelDoc_factor end)+       globalsla*((maxtpprio-tpprio+1)/maxtpprio) rank " // green
            + "from "
            + "(select frageid, teilprojektid, channel, sysdate - nvl(GLOBAL_inslauntil,STARTED) globalsla "
            + " from EBMF_FRAGEMETA "
            + " where ENDED is null "
    // 14.01.2015 (R.Koch) - Es sollen Fragen beliebiger Agentur gepusht werden
            //+ "  and ASSIGNED_AGENTURID=(select agentur from  ar_mitarbeiter where ar_mitarbeiter.MXID=?)" //1-Mitarbeiterid
            + "   and ASSIGNED_AGENTURID>0"
            + ") o, "
            + "(select  z.mitarbeiterid, teilprojektid, z.prozent/100 skill_factor, tp.PRIO tpprio, "
            + "         (select min(case  when min(liegezeit)<=0 then 1 else min(liegezeit) end ) minliegezeit from AR_AGENTUR_LIEGEZEITEN where agentur=am.agentur group by agentur) / (select max(liegezeit) from AR_AGENTUR_LIEGEZEITEN where typ =0 and agentur=am.agentur) channelMail_factor, "
            + "         (select min(case  when min(liegezeit)<=0 then 1 else min(liegezeit) end ) minliegezeit from AR_AGENTUR_LIEGEZEITEN where agentur=am.agentur group by agentur) / (select max(liegezeit) from AR_AGENTUR_LIEGEZEITEN where typ <>0 and agentur=am.agentur)    channelDoc_factor "
            + "  from ZUSTAENDIGKEIT z, ar_mitarbeiter am,  teilprojekt tp "
            + "  where z.mitarbeiterid=? and am.mxid=z.mitarbeiterid and tp.id=z.TEILPROJEKTID and tp.geloeschtam=0 and z.prozent>0   " //2-Mitarbeiterid
            + "       and not exists (select * from ar_agentur_blacklist b where b.agentur=am.AGENTUR and tp.id=b.teilprojekt)" 
            + "       and     exists (select * from rechte r where z.mitarbeiterid=r.mitarbeiterid and r.bezeichnung= 'operatorrecht' and r.parameter=tp.id and r.parameter=tp.id  ) ) q,"
            + "  frage, (select max(prio) maxtpprio from teilprojekt where geloeschtam=0  ) mtp   "
            + "where q.teilprojektid=o.teilprojektid "
            + "  and frage.id=o.frageid and frage.gesperrtvon=0"
            + "  and frage.TEILPROJEKTID>0 " //von Maildaemon gesprrt
            + "  and %s " //responsibility
            + "      frage.GELOESCHTAM=0 and frage.status ='wiedervorlage' "
            + "  and frage.wiedervorlagezeit < ? " //3 - Systime
            + "  and frage.reserviertfuer in ( 0, ?) " // 4  -Mitarbeiterid
            + "  order by " //rank desc, "
            + " frageid asc";

    @Override
    public Question getNextQuestion(Connection con, int operatorId, Operator operator, String responsibility, List<Integer> languages, List<Integer> doctypes, int nDays, int nCustomerId, long nQuestionDate, String role)
            throws SQLException, NoQuestionsWaitingException {
        try {
            //SkyLogger.getMediatrixLogger().debug("check for new Question: op:"+operatorId+" r:"+responsibility+" lang:"+languages.toArray().toString()+" dt:"+doctypes+" ndays:"+nDays+" nCustomerId:"+nCustomerId+ " qdate:"+nQuestionDate+" role:"+role);
            Integer questionId = getNextQuestionFromDB(con, GETNEXTWVQUESTION_SQL, operatorId, responsibility,true);
            if (questionId == null || questionId < 1) {
                //SkyLogger.getMediatrixLogger().debug("rn: >"+responsibility+"< op:" + operatorId+" "+System.currentTimeMillis()+" sql:" + GETNEXTWVQUESTION_SQL+" sql2:" + GETNEXTQUESTION_SQL );
                questionId = getNextQuestionFromDB(con, GETNEXTQUESTION_SQL, operatorId, responsibility,false);
            }//else{
             //   SkyLogger.getMediatrixLogger().debug("rok: >"+questionId+" >"+responsibility+"< op:" + operatorId+" "+System.currentTimeMillis()+" sql:" + GETNEXTWVQUESTION_SQL+" sql2:" + GETNEXTQUESTION_SQL );
           // }
            if (questionId != null && questionId > 0) {
                SkyLogger.getMediatrixLogger().debug("Pushing Question " + questionId + " for op:" + operatorId);
                return API.getServerAPI().getQuestionAPI().load(con, questionId, true);
            }
        } catch (Exception e) {
            SkyLogger.getMediatrixLogger().debug("r: >"+responsibility+"< op:" + operatorId+" "+System.currentTimeMillis()+" sql:" + GETNEXTWVQUESTION_SQL+" sql2:" + GETNEXTQUESTION_SQL );
            SkyLogger.getMediatrixLogger().error("Problem getting NextQuestion:"+e.getMessage() , e );
        }
        //SkyLogger.getMediatrixLogger().debug("Pushing: no question found for op:" + operatorId+"r: >"+responsibility+"<");
        return null;
    }

    private Integer getNextQuestionFromDB(Connection con, String sql, int operatorId, String responsibility, boolean vw) throws Exception {
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            String esql=String.format(sql,responsibility);
            //SkyLogger.getMediatrixLogger().debug("Pushing: sql:" + esql);
            pStmt = con.prepareStatement(esql);
            int i=1;
            //pStmt.setLong(i++, operatorId);
            pStmt.setLong(i++, operatorId);
            if (vw){
                pStmt.setLong(i++, System.currentTimeMillis() );
                pStmt.setLong(i++, operatorId);
            }
            pStmt.setMaxRows(1);
            rs = pStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pStmt != null) {
               
                pStmt.close();
            }
        }
        return null;
    }
    
    @Override
    public String getName() {
        return CHANNEL_BASED_ROUTING;
    }

    @Override
    public int getRoutingId() {
        return ROUTINGID;
    }

    @Override
    public boolean prefetchAllowed() {
        return false;
    }

    @Override
    public Question prefetchQuestion(Connection arg0, int arg1, Operator arg2, String arg3, List<Integer> arg4, List<Integer> arg5, int arg6, int arg7, long arg8, String arg9) throws SQLException, NoQuestionsWaitingException {
        return null;
    }

    @Override
    public void register() {
    }
}
