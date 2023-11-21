ENV=${1:-dev}
. $ENV.env

cd ..

APP_NAME=olympics-data-loader
APP_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout -f pom.xml)
java -jar ./target/$APP_NAME-$APP_VERSION.jar

cd scripts