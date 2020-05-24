DROP FUNCTION IF EXISTS register;
DELIMITER $$
CREATE FUNCTION register(string varchar(1000))
	RETURNS varchar(5)
    DETERMINISTIC
    BEGIN
		DECLARE nb varchar(50);
        DECLARE nbContacto varchar(30);
        DECLARE telf varchar(15);
        DECLARE fax varchar(15);
        DECLARE adress varchar(50);
        DECLARE ciudad varchar(50);
        DECLARE cod int;
        DECLARE toReturn varchar(5);
        DECLARE isValid varchar (50);
        
        #vamos actualizando el string quitando los valores ya utilizados..
			set nb:=(substring_index(string,"/",1));
		set string:=(substring(string,length(nb)+2));
			set nbContacto:=(substring_index(string,"/",1));
		set string:=(substring(string,length(nbContacto)+2));
			set telf:=(substring_index(string,"/",1));
		set string:=(substring(string,length(telf)+2));
			set fax:=(substring_index(string,"/",1));
		set string:=(substring(string,length(nbContacto)+2));
			set adress:=(substring_index(string,"/",1));
        set string:=(substring(string,length(nbContacto)+2));
			set ciudad:=(substring_index(string,"/",1));
            
        set isvalid:=(select nombre_cliente from cliente where nombre_cliente=nb);
        if(isValid IS NULL) then
			set cod:=(select max(codigo_cliente) from cliente)+1;
            INSERT INTO cliente (codigo_cliente,nombre_cliente,nombre_contacto,telefono,fax,linea_direccion1,ciudad,codigo_empleado_rep_ventas)
				values(cod,nb,nbContacto,telf,fax,adress,ciudad,8);
			set toReturn:="true";			
		else             
			set toReturn := "false";
		END IF;
    RETURN toReturn;    
    END $$
DELIMITER ;