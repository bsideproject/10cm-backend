insert into `user` (id, email, name, nickname) values (1, 'test1@test.com', '테스트1', '테스트1');
insert into `user` (id, email, name, nickname) values (2, 'test2@test.com', '테스트2', '테스트2');

insert into place(address, address_detail, latitude, longitude, name, user_id) values('주소1', '주소상세1', '1.1', '1.1', '장소1', 1);
insert into place(address, address_detail, latitude, longitude, name, user_id) values('주소2', '주소상세2', '1.1', '1.1', '장소2', 1);
insert into place(address, address_detail, latitude, longitude, name, user_id) values('주소3', '주소상세3', '1.1', '1.1', '장소3', 1);