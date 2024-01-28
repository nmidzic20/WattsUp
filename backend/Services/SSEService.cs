using backend.Data;
using backend.Models.Responses;
using Microsoft.EntityFrameworkCore;

namespace backend.Services
{
    public class SSEService
    {
        private TaskCompletionSource<List<ChargersResponse>?> _chargers = new TaskCompletionSource<List<ChargersResponse>?>();

        public void Reset()
        {
            _chargers = new TaskCompletionSource<List<ChargersResponse>?>();
        }

        public void NotifyChargerChange(List<ChargersResponse> chargers)
        {
            _chargers.TrySetResult(chargers);
        }

        public Task<List<ChargersResponse>?> WaitForChargerChange()
        {
            return _chargers.Task;
        }
    }
}
