# Please find a cool name

The goal of this project is to create a java program, that loads external Jar-Files containing a Backend written in Java
and also hosts the Frontend for the respective Backend. Planed features are also a login and permission system for these
sites and a database system.

### The structure would look somehow like this:
<ul>
    <li>
        example.com:4443/login - Universal Login Page for all sites
    </li>
    <li>
        example.com:4443/manage/... - The Page, where all the sub-pages and users are managed
    </li>
    <li>
        example.com:4443/foo/... - A page that either uses the universal login system or one without an login
    </li>
    <li>
        example.com:4443/bar/login/... - The Login page for the bar example, which has own users and doesn't use the
        universal login system
    </li>
    <li>
        example.com:4443/bar/page/... - The page itself for the bar example
    </li>
    <li>
        ...
    </li>
</ul>

# ToDo
Finished Tasks are marked with this ✔️ emoji. Once this program is actually usable the version number will start with
`1.` instead of `0.`
<ul>
<li>✔️ Write basic webserver, with ssl support.</li>
<li>Start with /manage/ page</li> 
<li>Build external jar loading system</li>
<li>Create sub page system</li>
<li>Config system</li>
<li>Account system</li>
<li>Database system</li>
</ul>
