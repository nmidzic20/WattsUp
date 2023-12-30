import { TestBed } from '@angular/core/testing';

import { SSEService } from './sse.service';

describe('SSEService', () => {
  let service: SSEService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SSEService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
