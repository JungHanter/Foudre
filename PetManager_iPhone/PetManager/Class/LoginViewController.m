//
//  LoginViewController.m
//  PetManager
//
//  Created by nil on 13. 3. 27..
//  Copyright (c) 2013년 PetManager. All rights reserved.
//

#import "LoginViewController.h"

@interface LoginViewController ()

@end

@implementation LoginViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    [self.emailTextField becomeFirstResponder];
}

- (IBAction)goLogin:(id)sender {
    
    NSLog(@"goLogin");
    
    if([self.emailTextField.text isEqualToString:@""] || [self.pwTextField.text isEqualToString:@""] ){
        
        UIAlertView *alertView = [UIAlertView alloc];
        alertView = [alertView initWithTitle:@"로그인 실패" message:@"아이디와 비밀번호를 모두 입력해주세요" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alertView show];
        
    }else{
        UIViewController *petManagerStoryBoard = [self.storyboard instantiateViewControllerWithIdentifier:@"PetManagerNaviStoryBoard"];
        [self presentViewController:petManagerStoryBoard animated:YES completion:nil];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
