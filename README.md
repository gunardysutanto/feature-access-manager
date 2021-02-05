# Feature Access Manager

## Introduction
This is the sample application that i developed using Spring Boot with H2 as a standalone database

## Getting Started
### Build the container image
Please run the command to build the container image

`docker build --rm -t feature-access-manager .`

### Run the container image
`docker run --name feature-access-manager -d -p 8080:8080 feature-access-manager`

### Start to use the feature
You can the tools such as: TestMace or Postman for these operations below.
- Determine the status of user access for any feature.
  ```
  url: http://{your local ip address}:8080/features?featureName={Put your feature name here}&email={Put the user e-mail here}
  ```
  **Method**: GET
  

- To enable/disable the user access for any feature.
  ``` 
  url: 'http://{your local ip address}:8080/features'
  ```
  **Method**: POST
  
  **Request Body**
  ```json
  {
     "featureName": "{Put the feature name here}",
     "email": "{Put the user e-mail here}",
     "enable": "{Put the status for user access(true or false) here}"
  }  
  ```  