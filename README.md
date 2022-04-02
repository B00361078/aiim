###Steps to launch from GitHub###

1. Clone the repository to your local drive. Branch is "master".
2. Ensure you have Eclipse 2019 or later.
3. Ensure the M2E software is included in Eclipse, if not follow this guide - https://www.vogella.com/tutorials/EclipseMaven/article.html
4. Ensure Eclipse Java compiler is pointing to Java 1.8 jdk or jre.
5. In Eclipse package explorer select Import projects, then select Maven>Existing maven projects then select the directory of the downloaded
repository. Select "Add project(s) to working set". Dependencies will automatically download to your USER_HOME directory.
6. Locate the Main.java class located under com.aiim.app package. Right click then Run As>Run configurations. 
Run as Java application. Project is "app" and Main class is "com.aiim.app.Main", this will launch the app.
7. Right click the top level pakcage in package explorer. Select Run As>Maven Install, this is a simple way for unit tests to run then auto integration tests.

###List of available users###


| username      | password      | role             |
| ------------- | ------------- | ---------------- |
| jsmt1010		| user1010      | agent            |
| ncam0310		| AvaArran      | owner (guidewire)|
| user2020		| user2020      | sysadmin         |
| userfin1		| userfin1      | owner (finance)  |
| usergen1		| usergen1      | owner (general)  |
| usertel1		| usertel1      | owner (telephony)|
| usersec1		| usersec1      | owner (security) |



