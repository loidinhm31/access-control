mysql -h localhost -P 3306 --protocol=tcp -u root -p

CREATE USER 'controladmin'@'%' IDENTIFIED BY '123456';

GRANT ALL PRIVILEGES ON controldb.* TO 'controladmin'@'%';

