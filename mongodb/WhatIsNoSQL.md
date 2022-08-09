# What Is NoSQL ?

mongoDB는 관계형 데이터베이스가 아닌, NoSQL의 대표적 DB이다. <br/>
NoSQL이란 ?
* 고정된 테이블이 없다.
* JOIN이 없다. 
->> aggregate이 있음
* 빅데이터, 메시징, 세션 관리 등 비정형 데이터에 적합하다.

<br/>
SQL vs. NoSQL

|SQL(MySQL)|NoSQL(MongoDB)|
|------|---|
|규칙에 맞는 데이터를 입력|자유로운 데이터 입력 가능|
|테이블 간 JOIN 지원|컬렉션 간 JOIN 지원 X|
|안정성, 일관성|확장성, 가용성|
|용어(테이블, 로우, 컬럼)|용어(컬렉션, 다큐먼트, 필드)|

## 1. 컬렉션 생성
컬렉션은 따로 생성할 필요가 없다. <br/>
다큐먼트(document)를 넣는 순간 컬렉션도 자동 생성된다. <br/>
직접 생성하는 명령어도 있다. <br/>
```` bash
db.createCollection('users')
````

## 2. CRUD
*Create* <br/>
몽고디비는 컬럼을 정의하지 않아도 된다.
* 자유로움이 가장 큰 장점이나, 무엇이 들어올 지 모른다는 단점이 존재한다.
* 자바스크립트의 자료형과 유사하다
* ObjectId: 몽고디비의 자료형으로 고유 아이디 역할을 한다.
* `save()`로 저장한다.
```` bash
db.users.save({ name: 'dldmsql', age: 23, createdAt: new Date() });
````

컬렉션 간 관계를 강요하는 제한이 없으므로 직접 ObjectId를 넣어 연결한다. <br/>

*READ* <br/>
`find()` 혹은 `findOne()`으로 조회한다. <br/>
```` bash
db.users.find({});
````

두 번째 인수로 조회할 필드를 선택할 수 있다. <br/>
```` bash
db.users.find({}, {_id: 0, name: 1});
````
1은 추가, 0은 제외를 의미한다. <br/>
출력 결과는 아래와 같다. <br/>
```` bash
{"name" : "dldmsql" }
````

* 정렬은 `sort()` 메소드로 한다.
* 개수 제한은 `limit()`으로 한다.
* 건너뛸 다큐먼스 개수지정은 `skip()`으로 한다.

*UPDATE* <br/>
첫 번째 인수로 수정 대상을 찾고, 두 번째 인수로 수정 내용을 입력한다. <br/>
```` bash
db.users.update({name: "dnfmdql"}, {$set: {age: 22}});
````
결과로 수정된 개수가 출력된다. <br/>

*DELETE* <br/>
`remove()` 메소드로 삭제한다.
```` bash
db.users.remove({name: 'dldmsql'});
````
성공 시, 삭제된 개수가 반환된다.
