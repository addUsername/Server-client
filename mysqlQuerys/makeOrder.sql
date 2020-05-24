DROP FUNCTION IF EXISTS makeOrder;
DELIMITER $$
CREATE FUNCTION makeOrder(codCliente int, comentarios text, producto varchar(1000), canti varchar(1000), num int)
	RETURNS varchar (4)
    DETERMINISTIC
    BEGIN
		DECLARE codigoPedido INT;
        DECLARE productos varchar(1000);
        DECLARE cantidades varchar(1000);
        DECLARE prod_temp varchar(1000);
        DECLARE canti_temp varchar(1000);
        DECLARE precio decimal(15,2);
        
        
        SET codigoPedido:=((SELECT MAX(pedido.codigo_pedido) FROM pedido)+1);        
		INSERT INTO pedido (codigo_pedido,fecha_pedido,fecha_esperada,estado,comentarios,codigo_cliente) VALUES (codigoPedido,curdate(),curdate() + INTERVAL 5 DAY,'Pendiente',comentarios,codCliente);

        
        WHILE (num>0) DO
			SET num:=num-1;
            SET prod_temp:=(substring_index(producto,"/",1));
            SET producto:=(substring(producto,length(prod_temp)+2));
            
            SET canti_temp:=substring_index(canti,"/",1);
            SET canti:=substring(canti,length(canti_temp)+2);
            SET precio := (SELECT precio_venta FROM producto WHERE codigo_producto=prod_temp);
            
            INSERT INTO detalle_pedido(codigo_pedido,codigo_producto,cantidad,precio_unidad,numero_linea) VALUES (codigoPedido,prod_temp,canti_temp,precio,num);            
        END WHILE;
         
    RETURN "true";    
    END $$
DELIMITER ;