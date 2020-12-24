import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.MutableIssue

// retrieve the issue from the event
def issue = event.issue as MutableIssue

// only continue if the issue type is Incident
if (issue.issueType.name == 'Incident') {
    // retrieve the Impact and Urgency custom fields
    def impactCF = ComponentAccessor.customFieldManager.getCustomFieldObjectsByName('Impact')[0]
    def urgencyCF = ComponentAccessor.customFieldManager.getCustomFieldObjectsByName('Urgency')[0]
    
    // retrieve the values for the Impact and Urgency fields as Strings
    def impact = issue.getCustomFieldValue(impactCF).toString()
    def urgency = issue.getCustomFieldValue(urgencyCF).toString()
    
    // default priority will be 'Lowest'
    def priority = 'Lowest'
    
    // work out the correct priority
    switch (impact) {
        case 'High':
            if (urgency == 'High') priority = 'Highest'
            if (urgency == 'Medium') priority = 'High'
            if (urgency == 'Low') priority = 'Medium'
            break
        case 'Medium':
            if (urgency == 'High') priority = 'High'
            if (urgency == 'Medium') priority = 'Medium'
            if (urgency == 'Low') priority = 'Low'
            break
        case 'Low':
            if (urgency == 'High') priority = 'Medium'
            if (urgency == 'Medium') priority = 'Low'
            if (urgency == 'Low') priority = 'Lowest'
            break
    }
    
    // find the Priority object
    def newPriority = ComponentAccessor.constantsManager.priorities.find { it.name == priority }
    
    // change the priority of the issue
    issue.setPriority(newPriority)
    
    // update the issue to persist the changes
    ComponentAccessor.issueManager.updateIssue(event.user, issue, EventDispatchOption.DO_NOT_DISPATCH, false)
}