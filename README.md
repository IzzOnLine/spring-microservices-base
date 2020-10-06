# MICROSERVICES

- [registry-server](#registry-server)
- [config-server](#config-server)
- [gateway-server](#gateway-server)
- [security-oauth-service](#security-oauth-service)

- [microservice](#microservice)

- [Previous steps](#previous-steps)
- [How to use it?](#how-to-use-it)
- [Rest API documentation](#rest-api-documentation)
- [Postman Collection](https://github.com/IzzOnLine/spring-microservices-postman-collection)

### registry-server (Eureka)

Server used to register all microservices included in this project. In this case, using Netflix Eureka each client can simultaneously act as a server, to replicate its status to a
connected peer. In other words, a client retrieves a list of all connected peers of a service registry and makes all further requests to any other services through a load-balancing
algorithm (Ribbon by default).
<br><br> 

### config-server

Configuration server used by the microservices included to get their required initial values like database configuration, for example. Those configuration values have been added
into the project:

* [spring-microservices-configurations](https://github.com/IzzOnLine/spring-microservices-configurations)

As we can see, there is an specific folder for every microservice and the important information is encoded (the next code is part of *microservice/microservice.yml* file):

```
spring:
  ## Spring DATASOURCE (DataSourceAutoConfiguration & DataSourceProperties)
  datasource:
    url: jdbc:postgresql://localhost:5432/microservice
    username: microservice
    # Using environment variable ENCRYPT_KEY=ENCRYPT_KEY
    # Getting the value with POST localhost:8888/encrypt and the password in its body
    password: "{cipher}c5c54009a56a0f215a208067a2b13189091c13480306c81ab68edfb22a6251ca"
```

To increase the security level, in *bootstrap.yml* file I have deactivated the decryption on **config.server**, sending the information encrypted and delegating in every microservice
the labour of decrypt it. That is the reason to include in their *pom.xml* file, the dependency:

```
<dependency>
   <groupId>org.springframework.security</groupId>
   <artifactId>spring-security-rsa</artifactId>
</dependency>
```
<br>

### gateway-server

Using Zuul, this is the gateway implementation used by the other microservices included in this proof of concept. This module contains a filter to registry every web service invoked,
helping to debug every request.
<br><br>

### security-oauth-service

Full integration with Oauth 2.0 + Jwt functionality provided by Spring, used to have an option to manage authentication/authorization functionalities through access and refresh
tokens. With this microservice working as Oauth server we will be able to configure the details of every allowed application using the table in database:
**security.oauth_client_details**. On the other hand, several customizations have been included to the manage the creation of both JWT tokens and how to append additional information
too.
 
The technologies used are the following ones:

* **Hibernate** as ORM to deal with the PostgreSQL database.
* **JPA** for accessing, persisting, and managing data between Java objects and database.
* **Lombok** to reduce the code development in entities and DTOs.
* **Cache2k** as cache to reduce the invocations to the database.

In this microservice, the layer's division is:

* **repository** layer used to access to the database.
* **service** containing the business logic.

On the other hand, there are other "important folders": 

* **configuration** with several classes used to manage several areas such: security, exception handlers, cache, etc.
* **model** to store the entities.
* **dto** custom objects to contain specific data.
<br><br>

To use the encryption in postgres database you have to enable the pgcrypto extension and set the key in the postgresql.conf file see [Previous steps](#previous-steps) to test the login you have to insert a user first

### microservice

The main purpose of this microservice is the creation of a small one on which I am using the following
technologies:

* **Lombok** to reduce the code development in models and DTOs.
* **MVC** a traditional Spring MVC Rest API to manage the included requests.
  **Feign** to make Rest call

In this microservice, the layer's division is:

* **dao** layer used to access to the database.
* **service** containing the business logic.
* **controller** REST Api using Spring MVC.

On the other hand, there are other "important folders": 

* **configuration** with several classes used to manage several areas such: exception handlers, etc.
* **model** to store the Java objects that match with the tables in database.
* **dto** custom objects to contain specific data.
* **util/converter** to translate from models to dtos and vice versa.
<br><br>

## Previous steps

Due to every microservice has to decrypt the information sent by **config-server**, some steps are required:

#### Setting up an encryption key

In this project a symmetric encryption key has been used. The symmetric encryption key is nothing more than a shared secret that's used by the encrypter to encrypt a value
and the decrypter to decrypt a value. With the Spring Cloud configuration server, the symmetric encryption key is a string of characters you select that is passed to the
service via an operating system environment variable called **ENCRYPT_KEY**. For those microservices, I have used:

```
ENCRYPT_KEY=ENCRYPT_KEY
```

#### JDK and Oracle JCE

If you are using Oracle JDK instead of OpenJDK, you need to download and install Oracle's Unlimited Strength Java Cryptography Extension (JCE). This isn't available through
Maven and must be downloaded from Oracle Corporation. Once you've downloaded the zip files containing the JCE jars, you must do the following:

- Locate your `$JAVA_HOME/jre/lib/security` directory

- Back up the `local_policy.jar` and `US_export_policy.jar` files in the `$JAVA_HOME/jre/lib/security` directory to a different location.

- Unzip the JCE zip file you downloaded from Oracle

- Copy the `local_policy.jar` and `US_export_policy.jar` to your `$JAVA_HOME/jre/lib/security` directory.

#### Problems resolution

If you receive some errors related with encryption like:

```
IllegalStateException: Cannot decrypt: ...
```

Probably you have not set the ENCRYPT_KEY=ENCRYPT_KEY environment variable. Please, take a look to the previous steps in this section, maybe one of them is missing. If you still see same error messages, the best way to solve it is changing the
"cipher values" added in the microservices configuration files included in: 

* [spring-microservices-configurations](https://github.com/IzzOnLine/spring-microservices-configurations)

Like:

```
spring:
  datasource:
    # Raw password: microservice
    password: "{cipher}c5c54009a56a0f215a208067a2b13189091c13480306c81ab68edfb22a6251ca"
```

And database table `security.jwt_client_details`, in the column `signature_secret`.

To do it:

- Run **registry-server** and **config-server**

- Encrypt required values using the provided endpoint for that purpose, as follows: 

![Alt text](/documentation/Encryption.png?raw=true "Encryption endpoint")

- Overwrite current values by the provided ones.


## How to use it?

We first need to enable the pgcrypto extension. For this purpose, we need to execute the following statement:
```
CREATE EXTENSION pgcrypto;
```
The pgcrypto will allow us to use the pgp_sym_encrypt and pgp_sym_decrypt functions.
After the extension is enabled we have to set the encryption key in the postgresql.conf configuration file adding:
```
encrypt.key = 'Wow! So much security.'
```
We need also create the database tables that serves our application:

```
create schema security;

create table security.oauth_client_details (
  client_id                 varchar(64)     constraint oauth_client_details_pk primary key,
  resource_ids              varchar(256),
  client_secret             varchar(128)    not null,
  scope                     varchar(256),
  authorized_grant_types    varchar(256),
  web_server_redirect_uri   varchar(256),
  authorities               varchar(256),
  access_token_validity     int             not null,
  refresh_token_validity    int             not null,
  additional_information    text,
  autoapprove               varchar(256)
);

CREATE TABLE "security"."user" (
	id int4 NOT NULL,
	created_by varchar(255) NULL,
	creation_date timestamp NULL,
	deleted bool NOT NULL,
	last_update timestamp NULL,
	modified_by varchar(255) NULL,
	active bool NULL DEFAULT false,
	email bytea NULL,
	"name" bytea NULL,
	"password" bytea NULL,
	"role" varchar(255) NULL,
	secret bytea NULL,
	two_fa_enabled bool NULL DEFAULT false,
	username bytea NULL,
	CONSTRAINT uk_sb8bbouer5wak8vyiiy4pf2bx UNIQUE (username),
	CONSTRAINT user_pkey PRIMARY KEY (id)
);

CREATE TABLE "security"."user" (
	id int4 NOT NULL,
	created_by varchar(255) NULL,
	creation_date timestamp NULL,
	deleted bool NOT NULL,
	last_update timestamp NULL,
	modified_by varchar(255) NULL,
	active bool NULL DEFAULT false,
	email bytea NULL,
	"name" bytea NULL,
	"password" bytea NULL,
	"role" varchar(255) NULL,
	secret bytea NULL,
	two_fa_enabled bool NULL DEFAULT false,
	username bytea NULL,
	CONSTRAINT uk_sb8bbouer5wak8vyiiy4pf2bx UNIQUE (username),
	CONSTRAINT user_pkey PRIMARY KEY (id)
);

CREATE TABLE "security".user_role (
	user_id int4 NOT NULL,
	role_id int4 NOT NULL,
	CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id)
);

ALTER TABLE "security".user_role ADD CONSTRAINT fk41vliann881wotgtfpxxvtdxc FOREIGN KEY (user_id) REFERENCES security."user"(id);
ALTER TABLE "security".user_role ADD CONSTRAINT fka68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES security.role(id);

INSERT INTO security.oauth_client_details (client_id, client_secret
                                          ,scope, authorized_grant_types
                                          ,web_server_redirect_uri, authorities
                                          ,access_token_validity, refresh_token_validity
                                          ,additional_information, autoapprove)
VALUES ('Spring5Microservices', '{bcrypt}$2a$10$NlKX/TyTk41qraDjxg98L.xFdu7IQYRoi3Z37PZmjekaQYAeaRZgO'   -- Raw password: Spring5Microservices
       ,'read,write,trust', 'implicit,refresh_token,password,authorization_code,client_credentials,mfa'
       ,null, null
       ,900, 3600
       ,null, true);

INSERT INTO "security"."user" (id, name, active, password, username, deleted, role )
VALUES (1, 
        pgp_sym_encrypt('Administrator',  current_setting('encrypt.key')),
		    true,
		    pgp_sym_encrypt('{bcrypt}$2a$10$qTOh9o5HxlXY6jM724XcrOV.mWhOyD3/.V7vuCOwnszwiLrj8wCCO',  current_setting('encrypt.key')),
		    pgp_sym_encrypt('admin',  current_setting('encrypt.key')),
		    false,
	      'ROLE_ADMIN');
	      
insert into "security"."role" values(1,'ROLE_ADMIN');
insert into "security"."role" values(2,'ROLE_USER');
insert into "security"."user_role" values(1,1);
insert into "security"."user_role" values(1,2);
```

Once we have finished, it will be necessary to run the following services (following the displayed ordination):

1. **registry-server**
2. **config-server**
3. **gateway-server**
4. **security-oauth-service** 
5. **microservice** 



