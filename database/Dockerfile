# Use the latest MS SQL Server 2022 image.
FROM postgres:alpine

# Create a config directory.
RUN mkdir -p /usr/config
WORKDIR /usr/config

# Copy config files to new directory.
COPY . /usr/config

# Grant scripts permissions to run.
RUN chmod +x /usr/config/*.sh

# Run the scripts.
ENTRYPOINT ["./entrypoint.sh"]