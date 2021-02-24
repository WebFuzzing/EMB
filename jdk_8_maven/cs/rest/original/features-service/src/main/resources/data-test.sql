
insert into Product (id, name) values (1, 'Product_1');
insert into Feature (id, name,product_id) values (1, 'Feature_1',1), (2, 'Feature_2',1), (5, 'Feature_3',1);
insert into Constraint_Requires (id, for_product_id, source_feature_name, required_feature_name ) values (1,1,'Feature_3','Feature_2');
insert into Product_Configuration (id, name,product_id, valid) values (1, 'Product_1_Configuration_1',1, 1),(2, 'Product_1_Configuration_2',1, 1);
insert into Product_Configuration_Actived_Features (in_configurations_id, actived_features_id) values (1,1);
insert into Product_Configuration_Actived_Features (in_configurations_id, actived_features_id) values (1,2);
insert into Product_Configuration_Actived_Features (in_configurations_id, actived_features_id) values (2,1);



insert into Product (id, name) values (2, 'Product_2');
insert into Feature (id, name,product_id) values (3, 'Feature_A',2), (4, 'Feature_B',2), (6, 'Feature_C',2);
insert into Constraint_Excludes (id, for_product_id, excluded_feature_name, source_feature_name) values (1,2,'Feature_B','Feature_C');
insert into Product_Configuration (id, name,product_id, valid) values (3, 'Product_2_Configuration_1',2, 1),(4, 'Product_2_Configuration_2',2, 1);
insert into Product_Configuration_Actived_Features (in_configurations_id, actived_features_id) values (3,3);
insert into Product_Configuration_Actived_Features (in_configurations_id, actived_features_id) values (3,4);
