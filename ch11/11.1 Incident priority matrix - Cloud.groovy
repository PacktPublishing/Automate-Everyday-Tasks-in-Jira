// get custom fields
def customFields = get("/rest/api/2/field")
        .asObject(List)
        .body
        .findAll { (it as Map).custom } as List<Map>

// find the IDs for the Impact and Urgency custom fields
def impactCFId = customFields.find { it.name == 'Impact' }?.id
def urgencyCFId = customFields.find { it.name == 'Urgency' }?.id

// get the Impact and Urgency values for this issue
def impact = issue.fields[impactCFId]
def urgency = issue.fields[urgencyCFId]

// set the default priority to 'Lowest'
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

// update the issue with the new priority
def resp = put("/rest/api/2/issue/${issue.key}")
        .header('Content-Type', 'application/json')
        .body([
        fields: [
                priority: [name:"${priority}"]
        ]
]).asString()