import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserService, User, CreateUserRequest } from '../../../services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss']
})
export class UserFormComponent implements OnInit {
  userForm: FormGroup;
  isEditMode = false;
  availableRoles: string[] = [];

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private dialogRef: MatDialogRef<UserFormComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { user?: User; roles: string[] }
  ) {
    this.availableRoles = data.roles || [];
    this.isEditMode = !!data.user;

    this.userForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', this.isEditMode ? [] : [Validators.required, Validators.minLength(6)]],
      roles: [[], Validators.required],
      enabled: [true]
    });
  }

  ngOnInit(): void {
    if (this.isEditMode && this.data.user) {
      this.userForm.patchValue({
        username: this.data.user.username,
        email: this.data.user.email,
        roles: this.data.user.roles,
        enabled: this.data.user.enabled
      });
      this.userForm.get('password')?.clearValidators();
      this.userForm.get('password')?.updateValueAndValidity();
    }
  }

  onSubmit(): void {
    if (this.userForm.valid) {
      const formValue = this.userForm.value;
      
      if (this.isEditMode && this.data.user?.id) {
        const updateData: Partial<User> = {
          username: formValue.username,
          email: formValue.email,
          roles: formValue.roles,
          enabled: formValue.enabled
        };
        
        if (formValue.password) {
          // If password is provided, include it in update
          this.userService.updateUser(this.data.user.id, updateData).subscribe({
            next: () => {
              this.snackBar.open('User updated successfully', 'Close', { duration: 3000 });
              this.dialogRef.close(true);
            },
            error: (error) => {
              this.snackBar.open('Error updating user', 'Close', { duration: 3000 });
            }
          });
        } else {
          this.userService.updateUser(this.data.user.id, updateData).subscribe({
            next: () => {
              this.snackBar.open('User updated successfully', 'Close', { duration: 3000 });
              this.dialogRef.close(true);
            },
            error: (error) => {
              this.snackBar.open('Error updating user', 'Close', { duration: 3000 });
            }
          });
        }
      } else {
        const newUser: CreateUserRequest = {
          username: formValue.username,
          email: formValue.email,
          password: formValue.password,
          roles: formValue.roles
        };

        this.userService.createUser(newUser).subscribe({
          next: () => {
            this.snackBar.open('User created successfully', 'Close', { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: (error) => {
            this.snackBar.open('Error creating user', 'Close', { duration: 3000 });
          }
        });
      }
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
