FROM --platform=linux/arm64 ubuntu:22.04

WORKDIR /opt/superset

COPY . .

RUN apt-get update && \
    apt-get install -y build-essential libssl-dev libffi-dev python3-dev python3-pip libsasl2-dev libldap2-dev default-libmysqlclient-dev && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN pip install --upgrade setuptools pip

RUN pip install apache-superset gevent psycopg2-binary pgsanity Pillow

EXPOSE 8088

RUN chmod a+x *.sh

CMD ["/opt/superset/docker-start.sh"]
