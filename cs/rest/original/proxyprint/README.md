# proxyprint-kitchen

<h3>Development Environment Set Up</h3>
<ul>
<li>Open pom.xml and comment the spring-boot-starter-tomcat scope tag. The dependency should look like this (Only if you want to run the project in the IDE):
<pre>
	&lt;dependency&gt;
    	&lt;groupId&gt;org.springframework.boot	&lt;/groupId&gt;
    	&lt;artifactId&gt;spring-boot-starter-tomcat	&lt;/artifactId&gt;
    	&lt;!-- Comment in development environment --&gt;
    	&lt;!-- 	&lt;scope&gt;provided	&lt;/scope&gt; --&gt;
	&lt;/dependency&gt;
</pre>
</li>
<li>Open application.properties and add a tmp folder to host the documents you submit on print requests. Add a line like this:
<pre>
documents.path=/path/where/u/want/the/files
</pre>
</li>
<li>
Use maven version 3 or greater to build the project.
</li>
</ul>

<h3>API Documentation</h3>
Swagger is integrated in kitchen, to see full API documentation open file <a href="https://github.com/ProxyPrint/proxyprint-kitchen/blob/master/docs/docs.html">docs/docs.html</a>. To generate documentation just run ./swagger under scripts and see the result under docs/ folder.

<h4>How to document Controller's methods</h4>
This is a simple sample on how to add information do the documentation:
```java
    @ApiOperation(value = "Returns a pricetable", notes = "This method returns a pricetable of a specific printshop.")
    @Secured({"ROLE_MANAGER","ROLE_USER"})
    @RequestMapping(value = "/printshops/{id}/pricetable", method = RequestMethod.GET)
    public String getPrintShopPriceTable(@PathVariable(value = "id") long id) {
        ...
        ...
    }
```
<br/>
<hr/>
<br/>

<h3>Database Population</h3>
To populate the database with some data just do:
```
curl -X POST localhost:8080/admin/seed
```
<br/>
<hr/>
<br/>

<h3>scripts</h3>
<p>Under the folder <b>scripts</b>, one may find some scripts that are usefull and increase speed of development enviroment set up:
<ul>
<li><b>ppdb</b> - Database utils like drop and create, or most frequent select operations.</li>
<li><b>ppdb-mac</b> - MacOS version for ppdb.</li>
<li><b>swagger</b> - Script that generates REST API documentation. <i>The server must be running</i>.</li>
</ul>
