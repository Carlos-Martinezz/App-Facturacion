/*Tabla clientes */
INSERT INTO clientes(nombre, apellido, email, create_at, foto) VALUES('Carlos', 'Martínez', 'carlos@gmail.com', '2017-08-28', '');
INSERT INTO clientes(nombre, apellido, email, create_at, foto) VALUES('Alberto', 'Henríquez', 'alberto@gmail.com', '2017-08-28', '');
INSERT INTO clientes(nombre, apellido, email, create_at, foto) VALUES('Juan', 'Henríquez', 'alberto@gmail.com', '2017-08-28', '');
INSERT INTO clientes(nombre, apellido, email, create_at, foto) VALUES('José', 'Henríquez', 'alberto@gmail.com', '2017-08-28', '');

/*Tabla productos */
INSERT INTO productos(nombre, precio, create_at) VALUES('Panasonic Pantalla LCD', 990, NOW());
INSERT INTO productos(nombre, precio, create_at) VALUES('Sony Camara', 123490, NOW());
INSERT INTO productos(nombre, precio, create_at) VALUES('Ipod', 9990, NOW());
INSERT INTO productos(nombre, precio, create_at) VALUES('Notebook', 2990, NOW());
INSERT INTO productos(nombre, precio, create_at) VALUES('Hewlet Packard', 2599, NOW());
INSERT INTO productos(nombre, precio, create_at) VALUES('Bianchi Bicilceta', 259, NOW());
INSERT INTO productos(nombre, precio, create_at) VALUES('Mica Cómoda', 299, NOW());

/*Tabla facturas */
INSERT INTO facturas(descripcion, observacion, cliente_id, create_at) VALUES('Factura equipos de oficina', null, 1, NOW());
INSERT INTO facturas_items(cantidad, factura_id, producto_id) VALUES(1, 1, 1);
INSERT INTO facturas_items(cantidad, factura_id, producto_id) VALUES(2, 1, 4);
INSERT INTO facturas_items(cantidad, factura_id, producto_id) VALUES(1, 1, 5);
INSERT INTO facturas_items(cantidad, factura_id, producto_id) VALUES(1, 1, 7);

INSERT INTO facturas(descripcion, observacion, cliente_id, create_at) VALUES('Factura Bicicleta', 'Alguna nota importante', 1, NOW());
INSERT INTO facturas_items(cantidad, factura_id, producto_id) VALUES(3, 2, 6);

/* Creando usuarios y roles */
INSERT INTO users(username, password, enabled) VALUES('Carlos', '$2a$10$PSPpJKSuZt.bImjhjiJGduOXn57hrRo1OAFl6XbAbrrbz8hKAmTZa', 1);
INSERT INTO users(username, password, enabled) VALUES('admin', '$2a$10$0HaQOuWzhYQn4crRYhwDf.v3W6BN1SSI5.RU60gjG2t83FkthR0ua', 1);

INSERT INTO authorities (user_id, authority) VALUES(1, 'ROLE_USER');
INSERT INTO authorities (user_id, authority) VALUES(2, 'ROLE_USER');
INSERT INTO authorities (user_id, authority) VALUES(2, 'ROLE_ADMIN');
