<cfcomponent>
	
    <cfset fields=array()>

	<cffunction name="field" returntype="component" access="private" output="no">
		<cfargument name="displayName" required="true" type="string">
		<cfargument name="name" required="true" type="string">
		<cfargument name="defaultValue" required="false" type="string" default="">
		<cfargument name="required" required="false" type="boolean" default="no">
		<cfargument name="description" required="false" type="any" default="">
		<cfargument name="type" required="false" type="string" default="text">
		<cfargument name="values" required="false" type="string" default="">
		<cfreturn createObject("component","Field").init(displayName,name,defaultValue,required,description,type,values)>
	</cffunction>
	<cffunction name="group" returntype="component" access="private" output="no">
		<cfargument name="displayName" required="true" type="string">
		<cfargument name="description" required="false" type="string" default="">
		<cfargument name="level" required="false" type="numeric" default="2">
		<cfreturn createObject("component","Group").init(displayName,description,level)>
	</cffunction>


	<cffunction name="getCustomFields" returntype="array">
    	<cfreturn fields>
    </cffunction>
</cfcomponent>