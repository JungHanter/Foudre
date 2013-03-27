//
//  PetManagerTests.m
//  PetManagerTests
//
//  Created by SeiJin on 13. 3. 27..
//  Copyright (c) 2013년 PetManager. All rights reserved.
//

#import "LoginTests.h"

@implementation LoginTests

- (void)setUp
{
    [super setUp];
    
    // Set-up code here.
}

- (void)tearDown
{
    // Tear-down code here.
    
    [super tearDown];
}


- (void)testLoginWithRegisteredUser
{
    int result = 0;
    NSString *userid;
    NSString *pwd;
    STAssertTrue(result == 1, [NSString stringWithFormat:@"ID: %@, PWD: %@로 실패하였습니다.", userid, pwd]);
}

- (void) testLoginWithUnregisteredUser
{
    int result = 0;
    NSString *userid;
    NSString *pwd;
    STAssertTrue(result == 1, [NSString stringWithFormat:@"ID: %@, PWD: %@로 실패하였습니다.", userid, pwd]);
}




- (void)testExample
{
    STFail(@"Unit tests are not implemented yet in PetManagerTests");
}

@end
