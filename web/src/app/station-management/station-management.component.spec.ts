import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StationManagementComponent } from './station-management.component';

describe('StationManagementComponent', () => {
  let component: StationManagementComponent;
  let fixture: ComponentFixture<StationManagementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StationManagementComponent]
    });
    fixture = TestBed.createComponent(StationManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
