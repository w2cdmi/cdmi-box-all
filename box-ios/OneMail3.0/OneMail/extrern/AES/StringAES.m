//
//  StringAES.m
//  Onebox
//
//  Created by CSE on 14-7-20.
//
//

#import "StringAES.h"
#import "AESCrypt.h"

#define AES_KEY  @"PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OFLIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDINGNEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."

@implementation StringAES

static StringAES *sharedStringAES = nil;

#pragma mark-method
- (id)init
{
    if(self = [super init])
    {
        
    }
    return self;
}

- (void)dealloc
{
    //[super dealloc];
}


+ (StringAES *)sharedInstance
{
    @synchronized(self){
        if(!sharedStringAES)
        {
            sharedStringAES = [[StringAES alloc] init];
        }
        
        return sharedStringAES;
    }
}


+ (NSString *)stringtoAES:(NSString *)originalString
{
    return [AESCrypt encrypt:originalString password:AES_KEY];
}

+ (NSString *)AEStoString:(NSString *)AESString
{
    return [AESCrypt decrypt:AESString password:AES_KEY];
}

@end
