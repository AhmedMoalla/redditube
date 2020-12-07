FROM dexidp/dex:v2.26.0

USER root
RUN ["mkdir", "-p", "/opt/dex/scripts"]
RUN ["mkdir", "-p", "/opt/dex/config"]
COPY ./scripts/wait-for-postgres.sh /opt/dex/scripts
RUN ["chmod", "+x", "/opt/dex/scripts/wait-for-postgres.sh"]
RUN apk --update add postgresql-client

EXPOSE 5556

ENTRYPOINT ["/opt/dex/scripts/wait-for-postgres.sh"]
CMD [""]