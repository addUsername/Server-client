DROP FUNCTION IF EXISTS login;
DELIMITER $$
CREATE FUNCTION login(string varchar(50))
	RETURNS varchar(5)
    DETERMINISTIC
    BEGIN
		DECLARE nb int;
        DECLARE pass varchar(50);
        DECLARE isvalid varchar(50);
        DECLARE toReturn varchar(5);
        
        set nb:=(substring_index(string,"/",1));
        set pass:=(substring(string,length(nb)+2));
        set isvalid:=( select nombre_cliente from cliente where codigo_cliente=nb and nombre_cliente=pass);
        
        if (isvalid like binary pass) then
			set toReturn:="true";
            else set toReturn:="false";
		end if;
        
    RETURN toReturn;    
    END $$
DELIMITER ;