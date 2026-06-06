### Discoverys:

## Disocveries i made when building this project

    1. Spring managment of Custom queries.
    Spring doesn't keep track of your custom query so if you use, the db will store your updated data but spring won't have it.
    SO if you call .save() it will overwrite your current db state with what spring has in the ram.

    This is because spring takes the raw string, replaces the injects the hash params and then directly fires it to dbms.
    And the data layer (jpa, sdn) doesn't have any idea about this, so if you do .save() the data layer wakes up from slumber
    and saves unsaved data. This might over-write your custom query.

    Rule:- Use either Memory managed state(getter & setter) or "Custom Queries(direct db ops)" in the same transaction(including nested transactions)