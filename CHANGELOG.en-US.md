# Changelog

All notable changes to choerodon-front-agile will be documented in this file.

## [0.8.0] - 2018-07-19

### Add

- Add test management function module
- Add test summary function
- Add test cycle function
- Add test case management function
- Add test report function
- Add test status management
- Users can manage their own test cases through the test management module
- Test cases can be hooked to agile modules

## [0.9.0] - 2018-08-17

### Add

#### 0.9.0 Significantly added features

- Add multi-language interface,can be used for multi-language switching with the platform
- Add dashboard display interface
- The execution list adds a quick pass button, and if the test passes, it is not necessary to click on the details to adjust the execution state
- Increase the cycle export function, the user can export the contents of the cycle to excel
- Increase cycle cross-version cloning, users can copy test cycle to other versions for reuse
- Test cycle details table telescopic display, optimize the table content display after the tree diagram is collapsed
- Add some unit tests
- Add some api tests
- Added name verification when creating test cases
- The problem number increases the url, and the user does not have to switch to the agile interface to view the defect
- The cycle url is added to the execution record in the use case details, and the user can jump directly in the execution form in the use case details
- The default search of `use case management`, you do not need to select the field and then select it
- `Cycle Details` intetface increase according to personnel screening function,users can filter assignees or performers
- Support for redirect to new defect page when associating defects

### Modify

#### 0.9.0 Significantly modify the feature

- Optimized query interfaces such as reports, test cycle, test steps, and defects
- Event message changed to saga mode
- Test status icon style change
- `Test Summary` page interface integration optimization
- `Test Case Management` page to increase the display content
- `Test case management` remove extra sort fields
- Test execution can be edit in table
- Test strp in `Test Case Management` can be edit in table
- Optimize the `Report` page layout,column width does not change due to expansion

### Fix

#### 0.9.0 Significant repair features

- Fix `test cycle` and step pagination display problem
- Fix the problem that the count after deleting the test case will not be cascaded deleted
- Fix the problem that the page after execution is not automatically refreshed globally
- Fix execution details interface width compatibility error causes the editor button not to be seen
- Fixed a problem with pagination data error in `report`

## [0.10.0] - 2018-09-30

### Add

#### 0.10.0 Significantly added features

- Support oracle database
- Added unit test
- Added `test plan` function
- Added `folder` function in test case management 

### Modify

#### 0.10.0 Significantly modify the feature

- Modify the report data source selection operation, currently only display the data that have test association
- Modify how to add test exection through test case
- Test loop added user filtering function

### Fix

#### 0.10.0 Significant repair features

- The defect association table supplements the `project_id` field

## [0.11.0] - 2018-11-16

### Add

#### 0.11.0 Significantly added features

- Test case can import through template excel file 
- Test case export function
- Test execution details page add before/next button

### Modify

#### 0.11.0 Significantly modify the feature

- Test execution export changed to asynchronous modification, adding progress bar
- Modify some interfaces for agile services
- Test case folder copying and moving support batch mode

### Fix

#### 0.11.0 Significant repair features

- the target of clone test phase in test plan modle support different version, cycle

## [0.12.0] - 2018-12-14

### Add

#### 0.12.0 Significantly added features

- Add `Automation Test` module

### Modify

#### 0.12.0 Significantly modify the feature

- Add assign in batches function in the `Test Plan` moudle
- Show the priority and add filter with priority in `Test Plan` and `Test Execution` moudle

### Fix

#### 0.12.0 Significant repair features

- Fix the problem in export as excel moudle
- Fix the bug of data  in `Test Plan` and `Test Execution` when switching project
- Fix the bug of data in test step when switching the previous\next in the execution details

### Remove

#### 0.12.0 Significantly remove the feature

- Remove the fix data interface used in version 0.10.0

## [0.13.0] - 2019-01-11

### Modify

#### 0.13.0 Significantly modify the feature

- Optimize the defect correlation function in `Test Execution`
- When importing test results, `Test Cycle` can auto scale its time range in `Automation Test` module.
- Automatically adapt the parent `Test Cycle` time when modifying the test phase time in `Test Plan` module.

### Fix

#### 0.13.0 Significant repair features

- Fix `Automated Test` does not update the status to a failed when an error occurs while running.
- Fix export error in `Test Case` when the version name contains spaces.
- Fix the problem that can not delete `Test Cycle` which created by `Automated Test`.