/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messegingSystem;

import java.util.TimerTask;

/**
 *
 * @author Daniel
 */
public class MyTimerTask extends TimerTask  {
     Long param;

     public MyTimerTask(Long param) {
         this.param = param;
     }

     @Override
     public void run() {
         // You can do anything you want with param 
     }
}
