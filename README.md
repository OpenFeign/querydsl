[![CircleCI](https://dl.circleci.com/status-badge/img/gh/OpenFeign/querydsl/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/OpenFeign/querydsl/tree/master) [![codecov](https://codecov.io/gh/OpenFeign/querydsl/graph/badge.svg?token=6yJhkzBRmx)](https://codecov.io/gh/OpenFeign/querydsl) ![Maven Central Version](https://img.shields.io/maven-central/v/io.github.openfeign.querydsl/querydsl-core)

## Querydsl

Querydsl is a framework which enables the construction of type-safe SQL-like queries for multiple backends including JPA, MongoDB and SQL in Java.

Instead of writing queries as inline strings or externalizing them into XML files they are constructed via a fluent API.

[![Querydsl](https://circleci.com/gh/OpenFeign/querydsl.svg?style=shield)](https://app.circleci.com/pipelines/github/OpenFeign/querydsl?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.openfeign.querydsl/querydsl-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.openfeign.querydsl/querydsl-core/)

## Why forking?

Querydsl is at best stale, at worse dead.  By the time I made this fork, last commit was one year old and last release over 2 years old.

I reach out to the queryDSL team, but, honestly, they don't care.

### Why forking under openfeign?

Well, openfeign is already stabilished and having querydsl under it gives a better idea on how committed I am on keeping the lights on.

### What's next?

Initial goals accomplished.

We now have a stable fork with regular releases.  Patches from community are happening and this project is moving idenpendently.

I'm here to help those who help themselves.  

What does it mean?

- If you send a PR, I will review, give an honest feedback, merge as soon I'm happy with it, and release as soon it's practical.
- If you create an issue, well, I'm unlikely to get involved.

I dedicate some of my free time to this project, so I will favor people that are contributing to it.

### What is the endgame?

Get a querydsl project active!  May be as a fork, may be as a wake up call to present querydsl project. If querydsl team gets their act thogether and pick up maintaining the project, this fork will most likely be archived.  If the want my help (which they don't at the present time) I can help.

### I need feature X? Or bug Z fixed?

Well, you will need to get your hands dirty.  I might fix bugs or create features, but mostly when they affect my day job. Sorry, just a single guy here doing free work on spare time.

**Getting started**

Use these tutorials to get started

* [Querying JPA](http://www.querydsl.com/static/querydsl/latest/reference/html/ch02.html#jpa_integration)
* [Querying SQL](http://www.querydsl.com/static/querydsl/latest/reference/html/ch02s03.html)
* [Querying Mongodb](http://www.querydsl.com/static/querydsl/latest/reference/html/ch02s07.html)
* [Querying Lucene](http://www.querydsl.com/static/querydsl/latest/reference/html/ch02s05.html)
* [Querying Collections](http://www.querydsl.com/static/querydsl/latest/reference/html/ch02s08.html)
* [Querydsl Spatial](http://www.querydsl.com/static/querydsl/latest/reference/html/ch02s04.html)
* [Querying JDO](http://www.querydsl.com/static/querydsl/latest/reference/html/ch02s02.html)

**Examples**

[Querydsl example projects](https://github.com/OpenFeign/querydsl/tree/master/querydsl-examples)

**Support**

Free support is provided in the [Discussion Section](https://github.com/querydsl/querydsl/discussions) and on [StackOverflow](http://stackoverflow.com/questions/tagged/querydsl).
Please do not post questions as issue. Such issues will be closed immediately.

**How to build**

Querydsl provides releases via public Maven repositories, but you can also build the sources yourself like this

```BASH
$ mvn -Pquickbuild,{projectname} clean install
```
Where projectname is one of the Maven profiles (e.g. `jpa`, `sql`, `mongodb`, etc. or `all`)

For more information visit the project homepage at https://querydsl.github.io.

**Docker Compose setup**

For running tests, a Docker Compose setup is provided. It comes with the following databases:

* Oracle Express Edition 11g
* PostgreSQL 16
* MySQL 5.5.34
* Cubrid 9.2

You will need to install [Docker] and [docker-compose].

To launch the database containers:

```BASH
$ docker-compose up -d
``` 

All of the databases' default ports are forwarded to the host machine.


**How to contribute**

GitHub pull requests are the way to contribute to Querydsl.

If you are unsure about the details of a contribution, ask on the Querydsl Google Group or create a ticket on GitHub.

[Docker]: https://www.docker.com/products/docker-desktop
[docker-compose]: https://docs.docker.com/compose/

### Slack

If you want to join Slack workspace for Querydsl contributors join by following [this link](https://join.slack.com/t/querydsl/shared_invite/zt-r7ufzz6q-zxIHgpOSSMFvoU3YU4SclQ).
