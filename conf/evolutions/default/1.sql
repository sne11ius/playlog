# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `post` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`title` text NOT NULL,`body` text NOT NULL,`date` DATE NOT NULL,`edited` DATE NOT NULL,`published` BOOLEAN NOT NULL,`author` VARCHAR(254) NOT NULL);

# --- !Downs

drop table `post`;

