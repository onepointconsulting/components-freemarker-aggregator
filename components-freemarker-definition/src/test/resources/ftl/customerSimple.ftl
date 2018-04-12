Your details:

First Name: ${givenName}
Family Name: ${familyName}
Birth date: ${birthDate?date}
ID: ${id}
<#if list??>
Tags: <#list list as tag>${tag},</#list>
</#if>
<#if test\-dash??>
    ${test\-dash}
</#if>