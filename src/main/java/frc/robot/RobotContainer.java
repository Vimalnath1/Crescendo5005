// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.PS4Controller.Button;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.AutoAmpSide;
import frc.robot.commands.AutoSourceSide;
import frc.robot.commands.AutofromCenter;
import frc.robot.commands.CenterRobot;
import frc.robot.commands.DefaultDrive;
import frc.robot.commands.DriveDistance;
import frc.robot.commands.DrivePastLineAutonomous;
import frc.robot.commands.FeedRing;
import frc.robot.commands.LineUptoTag;
import frc.robot.commands.MoveLoader;
import frc.robot.commands.OneClimb;
import frc.robot.commands.RobotClimb;
import frc.robot.commands.RunPiston;
import frc.robot.commands.ShootRing;
import frc.robot.commands.SpeedUptoShoot;
import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import java.util.List;
import java.util.function.DoubleSupplier;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathPlannerPath;

/*
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems
  private final DriveSubsystem m_robotDrive = new DriveSubsystem();
  private final ExampleSubsystem m_sub=new ExampleSubsystem();
  private final Mover m_mover=new Mover();
  private final Shooter m_shooter=new Shooter();
  private final Climber m_climber=new Climber();
  private final Feeder m_feeder=new Feeder();
  private final Pneumatics m_Pneumatics=new Pneumatics();
  private final AutofromCenter m_AutofromCenter=new AutofromCenter(m_robotDrive, m_shooter,m_feeder,m_sub);
  private final AutoAmpSide m_AutoAmpSide=new AutoAmpSide(m_robotDrive, m_shooter,m_feeder);
  private final AutoSourceSide m_AutoSourceSide=new AutoSourceSide(m_robotDrive, m_shooter,m_feeder);
  private final DrivePastLineAutonomous drivePastLineAutonomous=new DrivePastLineAutonomous(m_robotDrive);
  SendableChooser<Command> m_chooser = new SendableChooser<>();
  SendableChooser<Boolean> m_fieldorno = new SendableChooser<>();
  // The driver's controller
  XboxController xboxcontroller = new XboxController(OIConstants.kDriverControllerPort);
  CommandXboxController theXboxController=new CommandXboxController(1);
  CommandXboxController theXboxController2=new CommandXboxController(2);
  Joystick controller = new Joystick(0);
  XboxController controller2=new XboxController(2);
  DoubleSupplier ystick;

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();
    m_chooser.setDefaultOption("Center Auto", m_AutofromCenter);
    m_chooser.addOption("Blue Amp, Red Source", m_AutoAmpSide);
    m_chooser.addOption("Blue Sourcem, Red Amp", m_AutoSourceSide);
    m_chooser.addOption("Drive Forward", drivePastLineAutonomous);
    
    m_fieldorno.setDefaultOption("True", true);
    m_fieldorno.addOption("False", false);
    SmartDashboard.putData(m_chooser);
    SmartDashboard.putData(m_fieldorno);
    // putBoolean("null", false);
    // Configure default commands
    m_robotDrive.setDefaultCommand(
        // The left stick controls translation of the robot.
        // Turning is controlled by the X axis of the right stick.
        new RunCommand(
            () -> m_robotDrive.drive(
                MathUtil.applyDeadband(xboxcontroller.getLeftY(), OIConstants.kDriveDeadband),
                MathUtil.applyDeadband(xboxcontroller.getLeftX(), OIConstants.kDriveDeadband),
                MathUtil.applyDeadband(xboxcontroller.getRightX(), OIConstants.kDriveDeadband),
                m_fieldorno.getSelected()),
            m_robotDrive));
      // m_climbe
      // .setDefaultCommand(
        // new OneClimb(m_climber, xboxcontroller.getRightTriggerAxis(), ()->xboxcontroller.getLeftTriggerAxis())
        // );
        
        
        //Test driving with triggers
          // new DefaultDrive(m_robotDrive,
          //  ()->MathUtil.applyDeadband(xboxcontroller.getLeftX(), OIConstants.kDriveDeadband),
          //  ()->-MathUtil.applyDeadband(xboxcontroller.getRawAxis(2), OIConstants.kDriveDeadband),
          //  ()->MathUtil.applyDeadband(xboxcontroller.getRightTriggerAxis(), OIConstants.kDriveDeadband),
          //  ()->MathUtil.applyDeadband(xboxcontroller.getRightX(), OIConstants.kDriveDeadband)
          //  ));
        //If it doesn't work, try this:
        // new DefaultDrive(m_robotDrive,
        //  ()->MathUtil.applyDeadband(xboxcontroller.getLeftX(), OIConstants.kDriveDeadband),
        //  ()->MathUtil.applyDeadband(xboxcontroller.getLeftY(), OIConstants.kDriveDeadband),
        //  ()->0,
        //  ()->MathUtil.applyDeadband(xboxcontroller.getRightX(), OIConstants.kDriveDeadband)));
          
      }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by
   * instantiating a {@link edu.wpi.first.wpilibj.GenericHID} or one of its
   * subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then calling
   * passing it to a
   * {@link JoystickButton}.
   */
  private void configureButtonBindings() {
    new JoystickButton(xboxcontroller,1)
        .whileTrue(new RunCommand(
            () -> m_robotDrive.setX(),
            m_robotDrive));
    // new JoystickButton(xboxcontroller, 1).whileTrue(new RunCommand(()->m_robotDrive.driveX(),m_robotDrive));
    new JoystickButton(xboxcontroller, 1).whileTrue(new LineUptoTag(m_robotDrive));
    // new JoystickButton(xboxcontroller, 1).whileTrue(new DriveDistance(m_robotDrive, Units.inchesToMeters(1), 1));
    new JoystickButton(xboxcontroller, 2).onTrue(new SpeedUptoShoot(m_shooter, m_feeder, 0.5)); //Shoot Ring Amp 
    new JoystickButton(controller2, 3).whileTrue(new FeedRing(m_feeder,1));
    new JoystickButton(controller2, 4).whileTrue(new FeedRing(m_feeder,-1));
    new JoystickButton(xboxcontroller, 5).whileTrue(new ShootRing(m_shooter, -0.5)); //Load Ring 
    // new JoystickButton(xboxcontroller, 6).whileTrue(new ShootRing(m_shooter, 1)); //Shoot Ring Goal 
    new JoystickButton(xboxcontroller, 6).onTrue(new SpeedUptoShoot(m_shooter, m_feeder, 1));
    // new JoystickButton(xboxcontroller, 9).whileTrue(new MoveLoader(m_mover, 0.2)); //Drop loader (Might have to flip)
    // new JoystickButton(xboxcontroller, 10).whileTrue(new MoveLoader(m_mover, -0.2)); //Raise Loader (Might have to flip)
    
    theXboxController.povUp().whileTrue(new RobotClimb(m_climber, -1));
    theXboxController.povDown().whileTrue(new RobotClimb(m_climber, 1));
    new JoystickButton(xboxcontroller,7).whileTrue(new RunPiston(m_Pneumatics));
    new JoystickButton(controller2,7).whileTrue(new RunPiston(m_Pneumatics));
    new JoystickButton(xboxcontroller,8).whileTrue(new RunCommand(() -> m_robotDrive.zeroHeading(),m_robotDrive));
   
    new JoystickButton(controller2, 5).whileTrue(new OneClimb(m_climber, 0, 1));
    new JoystickButton(controller2, 6).whileTrue(new OneClimb(m_climber, -1, 0));
    theXboxController2.leftTrigger().whileTrue(new OneClimb(m_climber, 0, -1));
    theXboxController2.rightTrigger().whileTrue(new OneClimb(m_climber, 1, 0));
     // new JoystickButton(xboxcontroller, 2).whileTrue(new  RunCommand(()->m_robotDrive.getHeadings(),m_robotDrive));
    // new JoystickButton(xboxcontroller, 9).whileTrue(new LineUptoTag(m_robotDrive)); //This will probably be the final button for lining up. Change it to onTrue once it works
  
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    PathPlannerPath path= PathPlannerPath.fromPathFile("Align to Shooter");
    // List<PathPlannerPath> auto=PathPlannerAuto.getPathGroupFromAutoFile("Path Testing");
    // Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
    //   auto,List.of(new Translation2d(7, 0),new Translation2d(7, 0)),new Pose2d(7, 0, new Rotation2d(0)));
    // return AutoBuilder.buildAuto("Path Testing");
    // return AutoBuilder.followPath(path);
    return m_chooser.getSelected();
    // // An example trajectory to follow. All units in meters.
    // Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
    //     // Start at the origin facing the +X direction
    //     new Pose2d(0, 0, new Rotation2d(0)),
    //     // Pass through these two interior waypoints, making an 's' curve path
    //     List.of(new Translation2d(1, 1), new Translation2d(2, -1)),
    //     // End 3 meters straight ahead of where we started, facing forward
    //     new Pose2d(3, 0, new Rotation2d(0)),
    //     config);

    // var thetaController = new ProfiledPIDController(
    //     AutoConstants.kPThetaController, 0, 0, AutoConstants.kThetaControllerConstraints);
    // thetaController.enableContinuousInput(-Math.PI, Math.PI);

    // SwerveControllerCommand swerveControllerCommand = new SwerveControllerCommand(
    //     exampleTrajectory,
    //     m_robotDrive::getPose, // Functional interface to feed supplier
    //     DriveConstants.kDriveKinematics,

    //     // Position controllers
    //     new PIDController(AutoConstants.kPXController, 0, 0),
    //     new PIDController(AutoConstants.kPYController, 0, 0),
    //     thetaController,
    //     m_robotDrive::setModuleStates,
    //     m_robotDrive);

    // // Reset odometry to the starting pose of the trajectory.
    // m_robotDrive.resetOdometry(exampleTrajectory.getInitialPose());

    // // Run path following command, then stop at the end.
    // return swerveControllerCommand.andThen(() -> m_robotDrive.drive(0, 0, 0, false, false));
  }
}
