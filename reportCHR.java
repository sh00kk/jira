import com.atlassian.jira.issue.CustomFieldManager
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchException
import com.atlassian.jira.web.bean.PagerFilter
import org.apache.log4j.Level
import com.atlassian.jira.issue.history.ChangeItemBean
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.IssueFactory
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.util.ImportUtils
import com.atlassian.jira.config.ResolutionManager
import com.atlassian.jira.issue.Issue

import java.io.File
def newFile = new File(“//pach”)
def array = 
array.add([‘Номер’, ‘Тема’, ‘Описание’, ‘Эффект от внедрения’, ‘Эффект от внедрения (руб.)’, ‘MIR-код’, ‘Автор’, ‘Ответственный от Заказчика / Руководитель проекта’, ‘Основные ресурсы ИТ’, ‘НМА/Система’, ‘Категория проекта/задачи’, ‘Дата перехода в анализ’, ‘Дата перехода в Введен в эксплуатацию’, ‘Дата закрытия’, ‘Списания внутренние сотрудники в часах’, ‘Списания внешние сотрудники в часах’, ‘Бюджетная оценка стоимости разработки(руб)’, ‘НМА’, ‘Приказ’, ‘Команда’, ‘Текущий статус’])

CustomFieldManager cFManager = ComponentAccessor.getCustomFieldManager();
def changeHistoryManager = ComponentAccessor.changeHistoryManager

// The JQL query you want to search with
final jqlSearch = “key in (CHR-4087,CHR-3182,CHR-1896)”

// Some components
def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def searchService = ComponentAccessor.getComponentOfType(SearchService)

def issueManager = ComponentAccessor.getIssueManager()

def parseResult = searchService.parseQuery(user, jqlSearch)

def indexingService = ComponentAccessor.getOSGiComponentInstanceOfType(IssueIndexingService)

def changeItem
SimpleDateFormat df = new SimpleDateFormat(“yyyy.MM”);
SimpleDateFormat dm = new SimpleDateFormat(“yyyy-MM-dd”);

def results = searchService.search(user, parseResult.query, PagerFilter.unlimitedFilter)
def rsize = results.results.size()

for(int i = 0; i<= rsize-1; i++){
def issue = results.results[i]

def issues = issueManager.getIssueObject(issue.key)

changeItems = changeHistoryManager.getChangeItemsForField(issue, “status”)

log.warn(changeItems)

if (changeItems.reverse().findAll{df.format(it.created).toBigDecimal() <= 2023.12 & it.toString != “Closed” }.size() > 0 | changeItems.findAll{df.format(it.created).toBigDecimal() >= 2023.01 & it.toString != “Closed” }.size() > 0){

def toAnal = changeItems.findAll{it.toString == “Анализ”}.created
def toExpl = changeItems.findAll{it.toString == “Введен в эксплуатацию”}.created

def or = issue.getCustomFieldValue(“Основные ресурсы ИТ”)
def xr = 
def xp =
for(def orid : or){

if(orid.toString() == "Иное"){
 xr.add("Иное")
 xp.add("Иное")
 
}
    
  if(orid.toString() == "1C"){
 xr.add("КС1С-23")
 xp.add("№67-П")
 
}

 if(orid.toString() == "ASSSK"){
 xr.add("АСССК-23")
 xp.add("№70-П")
 
}


   if(orid.toString() == "BI"){
 xr.add("КХД-23")
 xp.add("№74-П")
}

    if(orid.toString() == "Risk"){
 xr.add("КЧ-23")
 xp.add("№69-П")
}

// Криф не нужен, убедиться что его нет
//        if(xx[xxx] == "Contact"){
//     xr.add("КРИФ-22"
//     xp.add("№10-4-п"     
//    }

    if(orid.toString() == "WEB"){
 xr.add("Сайт")
 xp.add("Сайт")
}

        if(orid.toString() == "KKA"){
 xr.add("ККА-23")
 xp.add("№73-П")
}

        if(orid.toString() == "ККО"){
 xr.add("ККА-23")
 xp.add("№73-П")
}

//    if(miss.key == "CHR-1805"){
// xr.add("Э-22"
// xp.add("№74-П"     
//}

//     if(miss.key == "CHR-919"){
//  xr.add("CRM-Corp-23"
//  xp.add("№71-П"     
// }
// if(miss.key == "CHR-639"){
//  xr.add("АСССК-23"
//  xp.add("№70-П" 
// }
   if(orid.toString() == "CRM"){
 xr.add("CRM16-23")
 xp.add("№72-П")
}
//issues.getCustomFieldValue(cFManager.getCustomFieldObjectByName(“Эффект от внедрения (руб.)”))

def key = issues.key.toString()
def summary = issues.summary
def description = issues.description.replaceAll(“\t” ,“” ).readLines()
def eff = issues.getCustomFieldValue(“Эффект от внедрения”)
def effrub = issues.getCustomFieldValue(“Эффект от внедрения (руб.)”)
//def mircod2 = issues.getCustomFieldValue(cFManager.getCustomFieldObjectsByName(“MIR-код”).toString()
def mircod = issues.getCustomFieldValue(“MIR-код”)
def reporter = issues.Reporter.username
def rukprj = issues.getCustomFieldValue(“Ответственный от Заказчика / Руководитель проекта”).username
def rit = issues.getCustomFieldValue(“Основные ресурсы ИТ”).toString()
def HMA = issues.getCustomFieldValue(“НМА/Система”).toString()
def kpz = issues.getCustomFieldValue(“Категория проекта/задачи”).toString()
def team = issues.getCustomFieldValue(“Команда”).toString()
def ins = “0”
def out = “0”
def rubrab = issues.getCustomFieldValue(“Бюджетная оценка стоимости разработки(руб)”)

if (rubrab != null){rubrab = rubrab.toLong()}

if (eff != null){eff = eff.replaceAll(“\t” ,“” ).readLines().toString()}
if (effrub == null) {effrub = 0}
//newFile.append( “\n”+ key +“\t”+ summary +“\t”+ description +“\t”+eff+“\t” + effrub.toInteger()+“\t”+mircod +“\t”+reporter+“\t”+rukprj+“\t”+ rit+“\t”+ HMA +“\t”+kpz +“\t”+toAnal +“\t”+toExpl+“\t”+ issues.resolutionDate +“\t”+ ins +“\t”+ out +“\t”+ rubrab +“\t”+ xr +“\t”+ xp +“\t”+ team +“\t”+ issues.status.name)
array.add([key, summary, description, eff, effrub.toInteger(), mircod, reporter, rukprj, rit, HMA, kpz, toAnal, toExpl, issues.resolutionDate, ins, out, rubrab, “0” , “0”, team, issues.status.name])

//if (effrub == null) {effrub = 0}
//newFile.append(“\n”+ key +“\t”+ effrub.toInteger())

}
  }
  }
final jqlSearch2 = “key in()”
def parseResult2 = searchService.parseQuery(user, jqlSearch2)
def results2 = searchService.search(user, parseResult2.query, PagerFilter.unlimitedFilter)
def rsize2 = results2.results.size()
for(int i = 0; i<= rsize2-1; i++){
def issue = results2.results[i]

def issues = issueManager.getIssueObject(issue.key)

def key = issues.key.toString()
def summary = issues.summary
def description = issues.description
if (description == null) { description = ‘’}
else {description = issues.description.replaceAll(“\t” ,“” ).readLines()}

def eff = issues.getCustomFieldValue(“Эффект от внедрения”)
def effrub = issues.getCustomFieldValue(“Эффект от внедрения (руб.)”)
//def mircod2 = issues.getCustomFieldValue(cFManager.getCustomFieldObjectsByName(“MIR-код”).toString()
def mircod = issues.getCustomFieldValue(“MIR-код”)
def reporter = issues.Reporter.username
def rukprj = “”
def rit = issues.getCustomFieldValue(“Основные ресурсы ИТ”).toString()
def HMA = issues.getCustomFieldValue(“НМА/Система”).toString()
def kpz = issues.getCustomFieldValue(“Категория проекта/задачи”).toString()
def team = issues.getCustomFieldValue(“Команда”).toString()
def ins = “0”
def out = “0”
def toAnal = ‘’
def toExpl = ‘’
def rubrab = issues.getCustomFieldValue(“Бюджетная оценка стоимости разработки(руб)”)
if (rubrab != null){rubrab = rubrab.toLong()}

if (eff != null){eff = eff.replaceAll(“\t” ,“” ).readLines().toString()}
if (effrub == null) {effrub = 0}

//newFile.append( “\n”+ key +“\t”+ summary +“\t”+ description +“\t”+eff+“\t” + effrub.toInteger()+“\t”+mircod +“\t”+reporter+“\t”+rukprj+“\t”+ rit+“\t”+ HMA +“\t”+kpz +“\t”+toAnal +“\t”+toExpl+“\t”+ issues.resolutionDate +“\t”+ ins +“\t”+ out +“\t”+ rubrab +“\t”+ “” +“\t”+ “” +“\t”+ team +“\t”+ issues.status.name)

array.add([key, summary, description, eff, effrub.toInteger(), mircod, reporter, rukprj, rit, HMA, kpz, toAnal, toExpl, issues.resolutionDate, ins, out, rubrab, “0” , “0”, team, issues.status.name])

}

newFile.withWriter(‘UTF-8’) { writer →
array.each { row →
writer << ‘\uFEFF’

    writer.write row.join(';')
    writer.write '\n'
   
 }
}

return ‘done’
