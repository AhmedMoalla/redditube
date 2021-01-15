CREATE TABLE IF NOT EXISTS MEDIA_POST (
    ID                  VARCHAR,
    TITLE               VARCHAR,
    OBJECT_ID           VARCHAR,
    THUMBNAIL_OBJECT_ID VARCHAR,
    IS_VIDEO            BOOLEAN,
    CREATION_DATE_TIME  TIMESTAMP,
    OWNER_ID            INT
);

ALTER TABLE MEDIA_POST
    ADD CONSTRAINT MEDIA_POST_PK PRIMARY KEY (ID),
    ADD CONSTRAINT MEDIA_POST_SUBSCRIBABLE_FK FOREIGN KEY (OWNER_ID) REFERENCES SUBSCRIBABLE(ID);
