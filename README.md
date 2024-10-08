## Running the Application

### 1. Clone the repository
```bash 
git clone https://github.com/DNaydenov/gateway.git
cd gateway
```

### 2. Create a jar
```bash
mvm package
```

### 3. Build and run the application using Docker Compose

```bash
docker-compose up --build
```

### 4. Access theApplication
- Spring Boot Application: http://localhost:8080
- RabbitMQ Management UI: http://localhost:15672 (default username: guest, password: guest)

## Configuration
- Use `application.properties` or edit/create environment variables in `compose.yaml`

## Troubleshooting

- When the maximum number of requests to the https://data.fixer.io are met the application will stop to fetch new date 
from it. In that case new key should be generated and the new value should be replaced for `api.fetch.access-key`   
  


