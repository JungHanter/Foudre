//
//  ViewController.m
//  PetManager
//
//  Created by SeiJin on 13. 3. 27..
//  Copyright (c) 2013년 PetManager. All rights reserved.
//

#import "IntroViewController.h"
#import "LoginViewController.h"

@interface IntroViewController ()

@end

@implementation IntroViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    

    
    //------------------------------------------------------------------
    // 3초 뒤 LoginView로 이동
    //------------------------------------------------------------------
    // [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(goLoginView) userInfo:nil repeats:NO];
}


//------------------------------------------------------------------
// LoginView로 이동
//------------------------------------------------------------------
-(void)goLoginView{
    
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
    UIViewController *vc = [storyboard instantiateViewControllerWithIdentifier:@"LoginViewController"];
    [vc setModalPresentationStyle:UIModalPresentationFullScreen];
    [self presentViewController:vc animated:NO completion:NULL];
    
    //LoginViewController *loginViewCont = [LoginViewController new];
    //[self presentViewController:loginViewCont animated:YES completion:nil];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
