-- MEMBER ���̺� ����
DROP TABLE MEMBER;

CREATE TABLE MEMBER(
     USERNO NUMBER  PRIMARY KEY,
     USERID VARCHAR2 (15)  NOT NULL UNIQUE,
     USERPWD  VARCHAR2(20)  NOT NULL,
     USERNAME VARCHAR2(20) NOT NULL,
     GENDER CHAR(1) CHECK (GENDER IN ('M', 'F')),
     AGE NUMBER,
     EMAIL VARCHAR2(30),
     PHONE CHAR(11),
     ADDRESS VARCHAR2(100),
     HOBBY VARCHAR2(50),
     ENROLLDATE DATE DEFAULT SYSDATE NOT NULL
);

-- ȸ����ȣ�� �߻���ų ������ ����
DROP SEQUENCE SEQ_USERNO;

CREATE SEQUENCE SEQ_USERNO
NOCACHE;

-- MEMBER ���̺� �׽�Ʈ������ ����
 INSERT INTO MEMBER
 VALUES( SEQ_USERNO.NEXTVAL, 'admin' , '1234', '������', 'M', 45
              , 'admin@naver.com','01012345678', '����� ��������', '������', '2021/01/25' );

 INSERT INTO MEMBER
 VALUES( SEQ_USERNO.NEXTVAL, 'user01' ,'pass01', 'ȫ�浿', 'F' , 23
              , NULL ,'01056781234', NULL , '��ȭ����', '2021/07/21' );

--�������� Ŀ�Խ������ �Ѵ�.
COMMIT;  

SELECT * FROM MEMBER;




