# Get JRE 21
FROM eclipse-temurin:21

# Copy the LEMURS API jar.
RUN mkdir -p /opt/App
COPY lemurs-api.jar /opt/app/lemurs-api.jar

# Copy the entrypoint script.
COPY entrypoint.sh /opt/
RUN tr -d '\015' </opt/entrypoint.sh >/opt/tmp.sh
RUN mv /opt/tmp.sh /opt/entrypoint.sh
RUN chmod +x /opt/entrypoint.sh

# Create the entrypoint.
ENTRYPOINT ["opt/entrypoint.sh"]