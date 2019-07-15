CREATE DATABASE LINK "NEWDB_PROD.SKY.DE"
  CONNECT TO "DMSVIEWS" IDENTIFIED BY VALUES '0553B55DD71BCB4A30EA66ECF775670885A822C12C05E8AB10' USING '(DESCRIPTION_LIST =     
(FAILOVER = yes)    
(LOAD_BALANCE = yes)    
(DESCRIPTION =       
(ADDRESS =         
(PROTOCOL = TCP)        
(HOST = 10.96.60.33)        
(PORT = 9551)        
(HOST = 10.96.60.34)        
(PORT = 9551)      
)      
(CONNECT_DATA =         
(SERVICE_NAME = TIBCOPRD_LB)        
(SERVER = dedicated)        
(FAILOVER_MODE =           
(BACKUP = PEAIPROD_LB)          
(TYPE = select)          
(METHOD = preconnect)          
(RETRIES = 20)          
(DELAY = 3)        
)      
)    
)  
)';