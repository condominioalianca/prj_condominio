

insert INTO TB_ENDERECO  (TX_ENDERECO,TX_ENDERECO_NUMERO,TX_ENDERECO_COMPLEMENTO,TX_BAIRRO, TX_CIDADE,TX_UF,TX_CEP)
VALUES ('RUA ARNALDO MARGONARI','180', 'AP11','JORDANOPOLIS', 'SAO BERNARDO DO CAMPO','SP','09894205');
insert INTO TB_ENDERECO ( TX_ENDERECO,TX_ENDERECO_NUMERO,TX_ENDERECO_COMPLEMENTO,TX_BAIRRO, TX_CIDADE,TX_UF,TX_CEP)
VALUES ('RUA ARNALDO MARGONARI','180', 'AP12','JORDANOPOLIS', 'SAO BERNARDO DO CAMPO','SP','09894205');
insert INTO TB_ENDERECO ( TX_ENDERECO,TX_ENDERECO_NUMERO,TX_ENDERECO_COMPLEMENTO,TX_BAIRRO, TX_CIDADE,TX_UF,TX_CEP)
VALUES ('RUA ARNALDO MARGONARI','180', 'AP21','JORDANOPOLIS', 'SAO BERNARDO DO CAMPO','SP','09894205');
insert INTO TB_ENDERECO ( TX_ENDERECO,TX_ENDERECO_NUMERO,TX_ENDERECO_COMPLEMENTO,TX_BAIRRO, TX_CIDADE,TX_UF,TX_CEP)
VALUES ('RUA ARNALDO MARGONARI','180', 'AP22','JORDANOPOLIS', 'SAO BERNARDO DO CAMPO','SP','09894205');
insert INTO TB_ENDERECO ( TX_ENDERECO,TX_ENDERECO_NUMERO,TX_ENDERECO_COMPLEMENTO,TX_BAIRRO, TX_CIDADE,TX_UF,TX_CEP)
VALUES ('RUA ARNALDO MARGONARI','180', 'AP31','JORDANOPOLIS', 'SAO BERNARDO DO CAMPO','SP','09894205');
insert INTO TB_ENDERECO ( TX_ENDERECO,TX_ENDERECO_NUMERO,TX_ENDERECO_COMPLEMENTO,TX_BAIRRO, TX_CIDADE,TX_UF,TX_CEP)
VALUES ('RUA ARNALDO MARGONARI','180', 'AP32','JORDANOPOLIS', 'SAO BERNARDO DO CAMPO','SP','09894205');
insert INTO TB_ENDERECO ( TX_ENDERECO,TX_ENDERECO_NUMERO,TX_ENDERECO_COMPLEMENTO,TX_BAIRRO, TX_CIDADE,TX_UF,TX_CEP)
VALUES ('RUA ARNALDO MARGONARI','180', 'AP31','JORDANOPOLIS', 'SAO BERNARDO DO CAMPO','SP','09894205');
insert INTO TB_ENDERECO  (TX_ENDERECO,TX_ENDERECO_NUMERO,TX_ENDERECO_COMPLEMENTO,TX_BAIRRO, TX_CIDADE,TX_UF,TX_CEP)
VALUES ('RUA ARNALDO MARGONARI','180', 'AP11','JORDANOPOLIS', 'SAO BERNARDO DO CAMPO','SP','09894205');


INSERT INTO tb_unidade (TX_NUMERO_UNIDADE,TX_ANDAR,TX_BLOCO) VALUES ('11','01','01');
INSERT INTO tb_unidade (TX_NUMERO_UNIDADE,TX_ANDAR,TX_BLOCO) VALUES ('11','01','01');
INSERT INTO tb_unidade (TX_NUMERO_UNIDADE,TX_ANDAR,TX_BLOCO) VALUES ('12','01','01');
INSERT INTO tb_unidade (TX_NUMERO_UNIDADE,TX_ANDAR,TX_BLOCO) VALUES ('21','02','01');
INSERT INTO tb_unidade (TX_NUMERO_UNIDADE,TX_ANDAR,TX_BLOCO) VALUES ('22','02','01');
INSERT INTO tb_unidade (TX_NUMERO_UNIDADE,TX_ANDAR,TX_BLOCO) VALUES ('31','03','01');
INSERT INTO tb_unidade (TX_NUMERO_UNIDADE,TX_ANDAR,TX_BLOCO) VALUES ('31','03','01');
INSERT INTO tb_unidade (TX_NUMERO_UNIDADE,TX_ANDAR,TX_BLOCO) VALUES ('32','03','01');


INSERT INTO tb_usuario (NM_USUARIO,TX_EMAIL,NR_TELEFONE_DDD, NR_TELEFONE, NR_CELULAR_DDD, NR_CELULAR, NR_DOCUMENTO_CPF,NR_DOCUMENTO_CNPJ, TX_TIPO_PESSOA, FL_ENVIA_BOLETO, FL_ENVIA_SMS, FL_ATIVO,ID_UNIDADE,ID_ENDERECO) VALUES
    ('DANILO LACERDA DE SOUZA FERREIRA','danilolsf@gmail.com',NULL,NULL,'11','998587401','31390705862',NULL,'F',TRUE,TRUE,TRUE,1,1);
INSERT INTO tb_usuario (NM_USUARIO,TX_EMAIL,NR_TELEFONE_DDD, NR_TELEFONE, NR_CELULAR_DDD, NR_CELULAR, NR_DOCUMENTO_CPF,NR_DOCUMENTO_CNPJ, TX_TIPO_PESSOA, FL_ENVIA_BOLETO, FL_ENVIA_SMS, FL_ATIVO,ID_UNIDADE,ID_ENDERECO) VALUES
    ('YOSHIHIDE OTA','jorgeota6@gmail.com',NULL,NULL,'11','998587401','06416231867',NULL,'F',TRUE,TRUE,TRUE,2,2);
INSERT INTO tb_usuario (NM_USUARIO,TX_EMAIL,NR_TELEFONE_DDD, NR_TELEFONE, NR_CELULAR_DDD, NR_CELULAR, NR_DOCUMENTO_CPF,NR_DOCUMENTO_CNPJ, TX_TIPO_PESSOA, FL_ENVIA_BOLETO, FL_ENVIA_SMS, FL_ATIVO,ID_UNIDADE,ID_ENDERECO) VALUES
    ('SANDRA NISHIDA','snishida.projetos@gmail.com',NULL,NULL,'11','998587401','25168066801',NULL,'F',TRUE,TRUE,TRUE,3,3);
INSERT INTO tb_usuario (NM_USUARIO,TX_EMAIL,NR_TELEFONE_DDD, NR_TELEFONE, NR_CELULAR_DDD, NR_CELULAR, NR_DOCUMENTO_CPF,NR_DOCUMENTO_CNPJ, TX_TIPO_PESSOA, FL_ENVIA_BOLETO, FL_ENVIA_SMS, FL_ATIVO,ID_UNIDADE,ID_ENDERECO) VALUES
    ('RAFAEL KASAKEVICIUS MARIN','rafael.kasakevicius.marin@hotmail.com',NULL,NULL,'11','998587401','22794084810',NULL,'F',TRUE,TRUE,TRUE,4,4);

INSERT INTO tb_usuario (NM_USUARIO,TX_EMAIL,NR_TELEFONE_DDD, NR_TELEFONE, NR_CELULAR_DDD, NR_CELULAR, NR_DOCUMENTO_CPF,NR_DOCUMENTO_CNPJ, TX_TIPO_PESSOA, FL_ENVIA_BOLETO, FL_ENVIA_SMS, FL_ATIVO,ID_UNIDADE,ID_ENDERECO) VALUES
    ('PATRICK JOSE DE MOURA','patrickmoura@gmail.com',NULL,NULL,'11','998587401','21958651800',NULL,'F',FALSE,FALSE,FALSE,5,5);
INSERT INTO tb_usuario (NM_USUARIO,TX_EMAIL,NR_TELEFONE_DDD, NR_TELEFONE, NR_CELULAR_DDD, NR_CELULAR, NR_DOCUMENTO_CPF,NR_DOCUMENTO_CNPJ, TX_TIPO_PESSOA, FL_ENVIA_BOLETO, FL_ENVIA_SMS, FL_ATIVO,ID_UNIDADE,ID_ENDERECO) VALUES
    ('MARCELO BAHRIJ CORREA','marcelo-bahrij@hotmail.com',NULL,NULL,'11','998587401','15541016894',NULL,'F',TRUE,TRUE,TRUE,6,6);

INSERT INTO tb_usuario (NM_USUARIO,TX_EMAIL,NR_TELEFONE_DDD, NR_TELEFONE, NR_CELULAR_DDD, NR_CELULAR, NR_DOCUMENTO_CPF,NR_DOCUMENTO_CNPJ, TX_TIPO_PESSOA, FL_ENVIA_BOLETO, FL_ENVIA_SMS, FL_ATIVO,ID_UNIDADE,ID_ENDERECO) VALUES
    ('ROSMEIRI FREITAS DE MOURA','patrickmoura@gmail.com',NULL,NULL,'11','998587401','22028286806',NULL,'F',TRUE,TRUE,TRUE,5,5);

INSERT INTO tb_usuario (NM_USUARIO,TX_EMAIL,NR_TELEFONE_DDD, NR_TELEFONE, NR_CELULAR_DDD, NR_CELULAR, NR_DOCUMENTO_CPF,NR_DOCUMENTO_CNPJ, TX_TIPO_PESSOA, FL_ENVIA_BOLETO, FL_ENVIA_SMS, FL_ATIVO,ID_UNIDADE,ID_ENDERECO) VALUES
    ('LUIS CLAUDIO CAVALHEIRO','teste@teste.com',NULL,NULL,'11','998587401','66910056868',NULL,'F',FALSE,FALSE,FALSE,5,5);