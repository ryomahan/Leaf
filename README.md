# Leaf for My Project

> There are no two identical leaves in the world.
>
> ​               — Leibnitz

## Introduction

Leaf refers to some common ID generation schemes in the industry, including redis, UUID, snowflake, etc.
Each of the above approaches has its own problems, so we decided to implement a set of distributed ID generation services to meet the requirements.
At present, Leaf covers Meituan review company's internal finance, catering, takeaway, hotel travel, cat's eye movie and many other business lines. On the basis of 4C8G VM, through the company RPC method, QPS pressure test results are nearly 5w/s, TP999 1ms.

You can use it to encapsulate a distributed unique id distribution center in a service-oriented SOA architecture as the id distribution provider for all applications

## Quick Start

### Leaf Server

Leaf provides an HTTP service based on spring boot to get the id

#### run Leaf Server

##### build

```shell
cd leaf
mvn clean install -DskipTests
cd leaf-server
```

##### run
###### maven

```shell
mvn spring-boot:run
```

or 
###### shell command

```shell
sh deploy/run.sh
```

##### test

```shell
#segment
curl http://localhost:8080/api/segment/get/leaf-segment-test

#snowflake
curl http://localhost:8080/api/snowflake/get/test
```

#### Configuration

Use environment variables as configuration items instead

| environment                       | meaning                           | default  |
| --------------------------------- | --------------------------------- | -------- |
| LEAF_SEGMENT_ENABLE               | whether segment mode is enabled   |          |
| LEAF_MYSQL_HOST                   | mysql host                        |          |
| LEAF_MYSQL_PORT                   | mysql port                        |          |
| LEAF_MYSQL_DATBASE                | mysql database                    |          |
| LEAF_MYSQL_USERNAME               | mysql username                    |          |
| LEAF_MYSQL_PASSWORD               | mysql password                    |          |
| LEAF_SNOWFLAKE_PORT               | zookeeper port                    |          |
| LEAF_SNOWFLAKE_ENABLE             | whether snowflake mode is enabled |          |
| LEAF_SNOWFLAKE_ZOOKEEPER_ADDRESS  | zookeeper address                 |          |

### Segment mode 

In order to use segment mode, you need to create DB table first, and configure leaf.jdbc.url, leaf.jdbc.username, leaf.jdbc.password

If you do not want use it, just configure leaf.segment.enable=false to disable it.

```sql
CREATE DATABASE leaf
CREATE TABLE `leaf_alloc` (
  `biz_tag` varchar(128)  NOT NULL DEFAULT '', -- your biz unique name
  `max_id` bigint(20) NOT NULL DEFAULT '1',
  `step` int(11) NOT NULL,
  `description` varchar(256)  DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`biz_tag`)
) ENGINE=InnoDB;

insert into leaf_alloc(biz_tag, max_id, step, description) values('leaf-segment-test', 1, 2000, 'Test leaf Segment Mode Get Id')
```
### Snowflake mode 

The algorithm is taken from twitter's open-source snowflake algorithm.

If you do not want to use it, just configure leaf.snowflake.enable=false to disable it.

Configure the zookeeper address

```
leaf.snowflake.zk.address=${address}
leaf.snowflake.enable=true
leaf.snowflake.port=${port}
```

configure leaf.snowflake.zk.address in the leaf.properties, and configure the leaf service listen port leaf.snowflake.port.

### monitor page

segment mode: http://localhost:8080/cache
