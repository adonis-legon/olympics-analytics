ENV=${1:-dev}
. $ENV.env

cd ..

APP_NAME=olympics-data-loader
APP_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout -f pom.xml)

docker rm ${APP_NAME} --force || true

docker run -d \
--name $APP_NAME \
-e DATASOURCE_URL=$DATASOURCE_URL \
-e DATASOURCE_USER=$DATASOURCE_USER \
-e DATASOURCE_PASS=$DATASOURCE_PASS \
-e EVENT_LOADER_SCHEDULE="$EVENT_LOADER_SCHEDULE" \
-e EVENT_LOADER_CONCURRENCY="$EVENT_LOADER_CONCURRENCY" \
--restart always \
$APP_NAME:$APP_VERSION

cd scripts

docker logs -f $APP_NAME