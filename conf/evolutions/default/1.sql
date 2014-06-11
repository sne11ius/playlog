# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `admindidentifier` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`socialId` BINARY(16) NOT NULL);
create table `post` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`title` text NOT NULL,`body` text NOT NULL,`date` TIMESTAMP NOT NULL,`edited` TIMESTAMP NOT NULL,`published` BOOLEAN NOT NULL,`author` VARCHAR(254) NOT NULL);

# --- !Downs

drop table `admindidentifier`;
drop table `post`;

