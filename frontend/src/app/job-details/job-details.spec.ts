import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobsDetail } from './jobs-detail';

describe('JobsDetail', () => {
  let component: JobsDetail;
  let fixture: ComponentFixture<JobsDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobsDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobsDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
