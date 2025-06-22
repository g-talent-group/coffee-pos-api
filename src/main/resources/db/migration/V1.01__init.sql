CREATE TABLE product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    price INT
);

CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_date DATE,
    total_amount INT
);

CREATE TABLE order_detail (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    quantity INT,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE ingredient (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    unit VARCHAR(20),
    stock DECIMAL(10, 2)
);

CREATE TABLE recipe (
     id INT PRIMARY KEY AUTO_INCREMENT,
     product_id INT,
     name VARCHAR(50),
     FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE recipe_detail (
     id INT PRIMARY KEY AUTO_INCREMENT,
     recipe_id INT,
     ingredient_id INT,
     quantity DECIMAL(10,2),
     FOREIGN KEY (recipe_id) REFERENCES recipe(id),
     FOREIGN KEY (ingredient_id) REFERENCES ingredient(id)
);

CREATE TABLE promotion (
     id INT PRIMARY KEY AUTO_INCREMENT,
     type VARCHAR(50),
     description VARCHAR(255),
     start_date DATETIME,
     end_date DATETIME
);

CREATE TABLE promotion_condition (
     id INT PRIMARY KEY AUTO_INCREMENT,
     promotion_id INT,
     condition_type VARCHAR(50),
     condition_value VARCHAR(50),
     FOREIGN KEY (promotion_id) REFERENCES promotion(id)
);

CREATE TABLE promotion_action (
     id INT PRIMARY KEY AUTO_INCREMENT,
     promotion_id INT,
     action_type VARCHAR(50),
     action_value VARCHAR(50),
     FOREIGN KEY (promotion_id) REFERENCES promotion(id)
);