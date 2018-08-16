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