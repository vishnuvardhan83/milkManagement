import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserService, User } from '../../../services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-role-assignment',
  templateUrl: './role-assignment.component.html',
  styleUrls: ['./role-assignment.component.scss']
})
export class RoleAssignmentComponent implements OnInit {
  roleForm: FormGroup;
  user: User;
  availableRoles = ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CUSTOMER'];
  selectedRoles: string[] = [];

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private dialogRef: MatDialogRef<RoleAssignmentComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { user: User }
  ) {
    this.user = data.user;
    this.selectedRoles = [...(data.user.roles || [])];
    
    this.roleForm = this.fb.group({
      roles: [this.selectedRoles]
    });
  }

  ngOnInit(): void {
    // Initialize with current user roles
    this.roleForm.patchValue({
      roles: this.selectedRoles
    });
  }

  toggleRole(role: string): void {
    const index = this.selectedRoles.indexOf(role);
    if (index > -1) {
      this.selectedRoles.splice(index, 1);
    } else {
      this.selectedRoles.push(role);
    }
    this.roleForm.patchValue({ roles: this.selectedRoles });
  }

  isRoleSelected(role: string): boolean {
    return this.selectedRoles.includes(role);
  }

  onSubmit(): void {
    if (this.user.id) {
      // Check if roles have changed
      const currentRoles = this.user.roles || [];
      const rolesChanged = 
        this.selectedRoles.length !== currentRoles.length ||
        !this.selectedRoles.every(role => currentRoles.includes(role));

      if (!rolesChanged) {
        this.snackBar.open('No changes to save', 'Close', { duration: 2000 });
        this.dialogRef.close(false);
        return;
      }

      // Update roles
      this.userService.updateRoles(this.user.id, this.selectedRoles).subscribe({
        next: (updatedUser) => {
          this.snackBar.open('Roles updated successfully', 'Close', { duration: 3000 });
          this.dialogRef.close(updatedUser);
        },
        error: (error) => {
          console.error('Error updating roles:', error);
          this.snackBar.open('Error updating roles', 'Close', { duration: 3000 });
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }

  formatRoleName(role: string): string {
    return role.replace('ROLE_', '').charAt(0) + role.replace('ROLE_', '').slice(1).toLowerCase();
  }
}
