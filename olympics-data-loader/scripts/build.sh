cd ..

APP_NAME=olympics-data-loader
APP_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout -f pom.xml)

docker build --tag $APP_NAME:$APP_VERSION .

cd scripts