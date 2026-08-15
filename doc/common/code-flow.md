## The high level code flow
1. First, define the request + response dto in /api layer
2. Define controller to API
3. Define command/query in /application
4. Define handler in /appication
5. Call handler from controller
6. If need to use DB, define method in repository + jpa repository in /infrastructure
7. In /application, please define interface that be implemented by /infrastructure repo, and handler use this interface

----
Note:
1. Please prior the shared/common component instead define new if it's exist
2. Keep clean architecture dependency cycle: api -> application -> domain (and infras is using through interface instead call direct)