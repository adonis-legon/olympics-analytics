# Application for Olympics Analytics

## Data Loader (Setup)

1. Go to olympics-data-loader directory and run the build.sh script to generate data loader app image
2. Create an environment file with the format {ENV}.env (e.g. dev.env) using the template file template.env as a reference
3. Execute the run.sh script passing the environment name (e.g. sh run.sh dev)

## Data Visualizer (Setup)

1. Go to olympics-visualizer directory and create an environment file with the format {ENV}.env (e.g. dev.env) using the template file template.env as a reference
2. Execute the start.sh script passing the environment name (e.g. sh start.sh dev)
3. Execute the stop.sh script if you want to stop the visualizer (Apache Superset)