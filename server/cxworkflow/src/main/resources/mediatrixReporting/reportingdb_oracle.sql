/* Create und alle Updates bis zum 14.12.2009 */

CREATE TABLE frage (
  id NUMBER(10,0) DEFAULT '0' NOT NULL,
  teilprojektid NUMBER(10,0) DEFAULT NULL,
  projektid NUMBER(10,0) DEFAULT NULL,
  globalerstatus char(255),
  status char(255),
  reserviert NUMBER(1,0) DEFAULT NULL,
  reserviertfuer NUMBER(10,0) DEFAULT NULL,
  eskalationsdatum NUMBER(19,0) DEFAULT NULL,
  geloeschtam NUMBER(19,0) DEFAULT NULL,
  weitergeleitetan char(200),
  wiedervorlagezeit NUMBER(19,0) DEFAULT NULL,
  servicecenter NUMBER(1,0) DEFAULT NULL,
  isEskalation NUMBER(1,0) DEFAULT NULL,
  prioritaet NUMBER(10,0) DEFAULT NULL,
  eskstufe NUMBER(10,0) DEFAULT NULL,
  bearbeitungsende NUMBER(19,0) DEFAULT NULL,
  bearbeitungszeit NUMBER(19,0) DEFAULT NULL,
  liegezeit NUMBER(19,0) DEFAULT NULL,
  wartezeit NUMBER(19,0) DEFAULT NULL,
  externezeit NUMBER(19,0) DEFAULT NULL,
  eingangsteilprojekt NUMBER(10,0) DEFAULT NULL,
  kundenservicelevel NUMBER(10,0) DEFAULT NULL,
  externservicelevel NUMBER(10,0) DEFAULT NULL,
  eskalationstart NUMBER(19,0) DEFAULT NULL,
  externweitergeleitet NUMBER(10,0) DEFAULT NULL,
  sprache NUMBER(10,0) DEFAULT NULL,
  zugeordnetvon NUMBER(10,0) DEFAULT NULL,
  erledigtvon NUMBER(10,0) DEFAULT NULL,
  docid varchar(255),
  comments varchar(255),
  abgelehnt NUMBER(10,0) DEFAULT NULL,
  tdb_timestamp DATE DEFAULT NULL,
  vorgangid NUMBER(10,0) DEFAULT NULL,
  emaildate NUMBER(19,0) DEFAULT NULL,
  stopzeit NUMBER(19,0) DEFAULT '0' NOT NULL,
  typ NUMBER(10,0) DEFAULT 0 NOT NULL,
  erledigtam NUMBER(19,0) DEFAULT 0,
  emailid NUMBER(19,0) DEFAULT 0 NOT NULL,
  autosplitted NUMBER(1,0),
  PRIMARY KEY(id)
);

CREATE INDEX frage_vorgangid ON frage(vorgangid);


CREATE TABLE mitarbeiter (
  id NUMBER(10,0) NOT NULL,
  name CHAR(255),
  loginname CHAR(100),
  email CHAR(100),
  aktiv NUMBER(1,0) NOT NULL,
  rolle CHAR(100),
  geloeschtam NUMBER(19,0) NOT NULL,
  extern NUMBER(10,0) NOT NULL,
  ueberwacht NUMBER(10,0) NOT NULL,
  user_id VARCHAR(255),
  rdb_timestamp DATE DEFAULT NULL,
  geloescht NUMBER(1,0)
) ;


CREATE TABLE mitarbeiterlog (
  id NUMBER(10,0) DEFAULT NULL,
  frageid NUMBER(10,0) DEFAULT NULL,
  aktiontyp VARCHAR(255),
  rdb_timestamp DATE DEFAULT NULL,
  mitarbeiterid NUMBER(10,0),
  antwortid NUMBER(10,0),
  aktionid NUMBER(10,0),
  aktionname VARCHAR(255),
  zeit DATE
);


CREATE TABLE projekt (
  id NUMBER(10,0) DEFAULT '0' NOT NULL,
  name VARCHAR(255),
  rdb_timestamp DATE DEFAULT NULL,
  geloescht NUMBER(1,0),
  PRIMARY KEY  (id)
);


CREATE TABLE schlagwort (
  id NUMBER(10,0) DEFAULT '0' NOT NULL,
  name VARCHAR(255),
  projektid NUMBER(10,0) DEFAULT NULL,
  geloeschtam NUMBER(19,0) DEFAULT NULL,
  parentid NUMBER(10,0) DEFAULT NULL,
  rdb_timestamp DATE DEFAULT NULL,
  geloescht NUMBER(1,0),
  PRIMARY KEY  (id)
);


CREATE TABLE schlagwortfrage (
  schlagwortid NUMBER(10,0) DEFAULT '0' NOT NULL,
  frageid NUMBER(10,0) DEFAULT '0' NOT NULL,
  count NUMBER(10,0) DEFAULT NULL,
  PRIMARY KEY  (schlagwortid,frageid)
) ;


CREATE TABLE tagmatch (
  frageid NUMBER(10,0) DEFAULT NULL,
  tmkey VARCHAR(255),
  tmvalue VARCHAR(255),
  tmpage NUMBER(10,0) DEFAULT NULL,
  rdb_timestamp DATE DEFAULT NULL
);


CREATE TABLE teilprojekt (
  id NUMBER(10,0) DEFAULT '0' NOT NULL,
  projektid NUMBER(10,0) DEFAULT NULL,
  name VARCHAR(255),
  rdb_timestamp DATE DEFAULT NULL,
  geloescht NUMBER(1,0),
  PRIMARY KEY  (id)
) ;

CREATE INDEX teilprojekt_projektid ON teilprojekt(projektid);

CREATE TABLE vorgang (
  id NUMBER(10,0) DEFAULT '0' NOT NULL,
  titel CHAR(255),
  rdb_timestamp DATE DEFAULT NULL,
  PRIMARY KEY  (id)
);

CREATE TABLE attachments (
  frageid NUMBER(10,0) DEFAULT NULL,
  filename VARCHAR(255),
  ordernumber NUMBER(10,0) DEFAULT NULL
);

CREATE TABLE profil (
  profilid NUMBER(10,0) DEFAULT '0' NOT NULL,
  profilname CHAR(255),
  geloeschtkz NUMBER(1,0) DEFAULT NULL,
  rdb_timestamp DATE DEFAULT NULL,
  PRIMARY KEY  (profilid)
);

CREATE TABLE mitarbeiter_profil (
  profilid NUMBER(10,0) DEFAULT NULL,
  mitarbeiterid NUMBER(10,0) DEFAULT NULL,
  rdb_timestamp DATE DEFAULT NULL
);

CREATE INDEX mitarbeiter_profil_profilid ON mitarbeiter_profil(profilid);
CREATE INDEX mitarbeiter_profil_mid ON mitarbeiter_profil(mitarbeiterid);
