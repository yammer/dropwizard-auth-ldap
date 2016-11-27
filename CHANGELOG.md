1.0.3 - Nov. 26, 2016
---
* `negotiateTls` controls whether or not the Ldap connection tries to negotiate TLS
* Dropwizard 1.0.5

1.0.2 - Sept 30, 2016
---
* Fixed a bug where `dropwizard-bom` wasn't specified correctly

1.0.1 - Sept 30, 2016
---
* Merged [pull request 5](https://github.com/yammer/dropwizard-auth-ldap/pull/5)
* Dropwizard 1.0.2
* Leverage `dropwizard-bom`
* Adding `maven-enforcer-plugin`

1.0.0 - July 26, 2016
----
* Dropwizard 1.0.0
* Targetting `Java8`

0.3.0 - Nov 5, 2015
-----
* Dropwizard 0.9.1
* Findbugs 3.0.2
* Updating maven-plugins

0.2.1
-----
* Dropwizard 0.8.4

0.2.0
-----
* Merged [pull request 8](https://github.com/yammer/dropwizard-auth-ldap/pull/8) to dropwizard 0.8.1
* Moved off 0.1.x for which will be reserved for dropward 0.7.x patches
* Updated `README` for new `AuthFactory` and `BasicAuthFactory` use in dropwizard 0.8.1

0.1.2
-----
* Merged [pull request 2](https://github.com/yammer/dropwizard-auth-ldap/pull/2) to add support for returning a `User` type that contains their group memberships intersecting with the `restrictGroups` supplied.

0.1.1
-----
* Dropwizard 0.7.1
* Upgrade to findbugs 2.5.4

0.1.0
-----
* Support for dropwizard 0.7.0

0.0.x
-----
* 
Now supports the deprecated 0.6.x dropwizard branch

0.0.19
------
1. Replaced `securityPrincipal` with `userFilter`.
2. Added `groupFilter` to allow for group membership filtering.
3. Configurable username, groupname, and group membership attributes.
4. Configurable group filtering set. If none are specified then fallback to previous behavior which was no group filtering.


0.0.18
------
Initial release
