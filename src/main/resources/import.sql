-- Dados de exemplo para desenvolvimento

-- Produtos
INSERT INTO product (id, name, description, price, quantity) VALUES (1, 'Notebook Dell', 'Notebook Dell Inspiron 15', 3500.00, 10);
INSERT INTO product (id, name, description, price, quantity) VALUES (2, 'Mouse Logitech', 'Mouse sem fio Logitech MX Master 3', 450.00, 25);
INSERT INTO product (id, name, description, price, quantity) VALUES (3, 'Teclado Mecânico', 'Teclado Mecânico RGB', 650.00, 15);
INSERT INTO product (id, name, description, price, quantity) VALUES (4, 'Monitor LG', 'Monitor LG 27" 4K', 2200.00, 8);
INSERT INTO product (id, name, description, price, quantity) VALUES (5, 'Webcam Logitech', 'Webcam Logitech C920 HD', 350.00, 20);
INSERT INTO product (id, name, description, price, quantity) VALUES (6, 'Headset Gamer', 'Headset Gamer RGB 7.1', 280.00, 30);
INSERT INTO product (id, name, description, price, quantity) VALUES (7, 'SSD Kingston', 'SSD Kingston 480GB SATA', 320.00, 40);
INSERT INTO product (id, name, description, price, quantity) VALUES (8, 'Memória RAM', 'Memória RAM DDR4 16GB 3200MHz', 450.00, 35);
INSERT INTO product (id, name, description, price, quantity) VALUES (9, 'Placa de Vídeo', 'GeForce RTX 3060 12GB', 2800.00, 5);
INSERT INTO product (id, name, description, price, quantity) VALUES (10, 'Processador AMD', 'AMD Ryzen 5 5600X', 1500.00, 12);

-- Clientes
INSERT INTO customer (id, name, email, phone, cpf, address, city, state, zipCode, createdAt, updatedAt) 
VALUES (1, 'João Silva', 'joao.silva@email.com', '(11) 98765-4321', '123.456.789-00', 'Rua das Flores, 123', 'São Paulo', 'SP', '01234-567', '2024-01-15 10:00:00', null);

INSERT INTO customer (id, name, email, phone, cpf, address, city, state, zipCode, createdAt, updatedAt) 
VALUES (2, 'Maria Santos', 'maria.santos@email.com', '(21) 98765-1234', '987.654.321-00', 'Av. Atlântica, 456', 'Rio de Janeiro', 'RJ', '22070-001', '2024-01-20 14:30:00', null);

INSERT INTO customer (id, name, email, phone, cpf, address, city, state, zipCode, createdAt, updatedAt) 
VALUES (3, 'Pedro Oliveira', 'pedro.oliveira@email.com', '(31) 98765-5678', '456.789.123-00', 'Rua Minas Gerais, 789', 'Belo Horizonte', 'MG', '31270-100', '2024-02-01 09:15:00', null);

INSERT INTO customer (id, name, email, phone, cpf, address, city, state, zipCode, createdAt, updatedAt) 
VALUES (4, 'Ana Costa', 'ana.costa@email.com', '(41) 98765-9876', '789.123.456-00', 'Rua XV de Novembro, 321', 'Curitiba', 'PR', '80020-000', '2024-02-10 16:45:00', null);

INSERT INTO customer (id, name, email, phone, cpf, address, city, state, zipCode, createdAt, updatedAt) 
VALUES (5, 'Carlos Ferreira', 'carlos.ferreira@email.com', '(51) 98765-2468', '321.654.987-00', 'Av. Ipiranga, 654', 'Porto Alegre', 'RS', '90160-090', '2024-02-15 11:20:00', null);

-- Pedidos
INSERT INTO customer_order (id, customer_id, status, totalAmount, orderDate, paymentDate, shippingDate, deliveryDate, notes, shippingAddress, shippingCity, shippingState, shippingZipCode) 
VALUES (1, 1, 'DELIVERED', 4150.00, '2024-03-01 10:00:00', '2024-03-01 10:30:00', '2024-03-02 08:00:00', '2024-03-05 14:00:00', 'Entregar no período da tarde', 'Rua das Flores, 123', 'São Paulo', 'SP', '01234-567');

INSERT INTO customer_order (id, customer_id, status, totalAmount, orderDate, paymentDate, shippingDate, deliveryDate, notes, shippingAddress, shippingCity, shippingState, shippingZipCode) 
VALUES (2, 2, 'SHIPPED', 3270.00, '2024-03-10 15:00:00', '2024-03-10 15:30:00', '2024-03-11 09:00:00', null, 'Cuidado: Frágil', 'Av. Atlântica, 456', 'Rio de Janeiro', 'RJ', '22070-001');

INSERT INTO customer_order (id, customer_id, status, totalAmount, orderDate, paymentDate, shippingDate, deliveryDate, notes, shippingAddress, shippingCity, shippingState, shippingZipCode) 
VALUES (3, 3, 'PROCESSING', 1500.00, '2024-03-15 11:00:00', '2024-03-15 11:15:00', null, null, null, 'Rua Minas Gerais, 789', 'Belo Horizonte', 'MG', '31270-100');

INSERT INTO customer_order (id, customer_id, status, totalAmount, orderDate, paymentDate, shippingDate, deliveryDate, notes, shippingAddress, shippingCity, shippingState, shippingZipCode) 
VALUES (4, 4, 'CONFIRMED', 2800.00, '2024-03-18 16:00:00', '2024-03-18 16:20:00', null, null, 'Presente de aniversário', 'Rua XV de Novembro, 321', 'Curitiba', 'PR', '80020-000');

INSERT INTO customer_order (id, customer_id, status, totalAmount, orderDate, paymentDate, shippingDate, deliveryDate, notes, shippingAddress, shippingCity, shippingState, shippingZipCode) 
VALUES (5, 5, 'PENDING', 450.00, '2024-03-20 09:30:00', null, null, null, null, 'Av. Ipiranga, 654', 'Porto Alegre', 'RS', '90160-090');

-- Itens dos Pedidos
-- Pedido 1 (Delivered)
INSERT INTO orderitem (id, order_id, product_id, quantity, unitPrice, subtotal) 
VALUES (1, 1, 1, 1, 3500.00, 3500.00);
INSERT INTO orderitem (id, order_id, product_id, quantity, unitPrice, subtotal) 
VALUES (2, 1, 3, 1, 650.00, 650.00);

-- Pedido 2 (Shipped)
INSERT INTO orderitem (id, order_id, product_id, quantity, unitPrice, subtotal) 
VALUES (3, 2, 7, 2, 320.00, 640.00);
INSERT INTO orderitem (id, order_id, product_id, quantity, unitPrice, subtotal) 
VALUES (4, 2, 8, 2, 450.00, 900.00);
INSERT INTO orderitem (id, order_id, product_id, quantity, unitPrice, subtotal) 
VALUES (5, 2, 6, 6, 280.00, 1680.00);

-- Pedido 3 (Processing)
INSERT INTO orderitem (id, order_id, product_id, quantity, unitPrice, subtotal) 
VALUES (6, 3, 10, 1, 1500.00, 1500.00);

-- Pedido 4 (Confirmed)
INSERT INTO orderitem (id, order_id, product_id, quantity, unitPrice, subtotal) 
VALUES (7, 4, 9, 1, 2800.00, 2800.00);

-- Pedido 5 (Pending)
INSERT INTO orderitem (id, order_id, product_id, quantity, unitPrice, subtotal) 
VALUES (8, 5, 2, 1, 450.00, 450.00);

-- Atualizar sequences para próximos IDs
ALTER SEQUENCE product_seq RESTART WITH 11;
ALTER SEQUENCE customer_seq RESTART WITH 6;
ALTER SEQUENCE customer_order_seq RESTART WITH 6;
ALTER SEQUENCE orderitem_seq RESTART WITH 9;