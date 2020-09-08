#import "SNLog.h"


@implementation SNLog


#pragma mark Singleton Methods
static SNLog *sharedInstance;
+ (SNLog *) logManager {

    if (sharedInstance == nil) {
        sharedInstance = [[SNLog alloc] init];
    }

    return sharedInstance;
}

+ (id) allocWithZone:(NSZone *)zone {
    if (sharedInstance == nil) {
        sharedInstance = [super allocWithZone:zone];
    }
    return sharedInstance;
}


+ (void) Log: (NSString *) format, ... {
    SNLog *log = [SNLog logManager];
    va_list args;
    va_start(args, format);
    NSString *logEntry = [[NSString alloc] initWithFormat:format arguments:args];
    [log writeToLogs: 1 :logEntry];
}


+ (void) Log: (LOG_LEVEL) logLevel : (NSString *) format, ... {
    SNLog *log = [SNLog logManager];
    va_list args;
    va_start(args, format);
    NSString *logEntry = [[NSString alloc] initWithFormat:format arguments:args];
    [log writeToLogs:logLevel :logEntry];

}
#pragma mark Instance Methods

- (void) writeToLogs: (LOG_LEVEL) logLevel : (NSString *) logEntry {
    NSString *formattedLogEntry = [self formatLogEntry:logLevel :logEntry];
    for (NSObject<SNLogStrategy> *logger in logStrategies) {
        if (logLevel >= logger.logAtLevel) {
            [logger writeToLog: logLevel: formattedLogEntry];
        }
    }

}

- (id) init {
    if (self = [super init]) {
        SNConsoleLogger *consoleLogger = [[SNConsoleLogger alloc] init];
        consoleLogger.logAtLevel = 0;
        [self addLogStrategy:consoleLogger];

        return self;
     } else {
         return nil;
     }


}

- (void) addLogStrategy: (id<SNLogStrategy>) logStrategy {
    if (logStrategies == nil) {
        logStrategies = [[NSMutableArray alloc] init];
    }

    [logStrategies addObject: logStrategy];
}


- (NSString *) formatLogEntry: (LOG_LEVEL) logLevel : (NSString *) logData {
    if (!logLevel) {
        return @"";
    }
    NSDate *now = [NSDate date];
    NSString* tag = @"Info";
    switch (logLevel) {
        case LDebug:
            tag = @"Debug";
            break;
        case LInfo:
            tag = @"Info";
            break;
        case LWarn:
            tag = @"Warn";
            break;
        case LError:
            tag = @"Error";
            break;
        case LFatal:
            tag = @"Fatal";
            break;
        default:
            tag = @"Unknown";
            break;
    }
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSString *formattedString = [dateFormatter stringFromDate:now];
    return [NSString stringWithFormat:@"%@ [%@] - %@", formattedString, tag, logData];
}

@end







@implementation SNConsoleLogger
@synthesize logAtLevel;

- (void) writeToLog:(NSInteger) logLevel :(NSString *)logData {
    printf("%s\r\n", [logData UTF8String]);
}

@end










@implementation SNFileLogger

@synthesize logAtLevel;

- (id) initWithPathAndSize: (NSString *) filePath : (NSInteger) truncateSize {
    logAtLevel = 2;

    if (self = [super init]) {
        logFilePath = filePath;
        truncateBytes = truncateSize;
        return self;
    } else {
        return nil;
    }
}

- (void) writeToLog:(NSInteger) logLevel :(NSString *)logData {
    
    Byte txtHeader[3] = {0xEF,0xBB,0xBF};
    NSData *logEntry =  [[logData stringByAppendingString:@"\r\n"] dataUsingEncoding:NSUTF8StringEncoding];
    NSFileManager *fm = [NSFileManager defaultManager];

    if(![fm fileExistsAtPath:logFilePath]) {
        NSMutableData* data = [NSMutableData dataWithBytes:txtHeader length:3];
        [data appendData:logEntry];
        [fm createFileAtPath:logFilePath contents:data attributes:nil];
    } else {
        NSDictionary *attrs = [fm attributesOfItemAtPath:logFilePath error:nil];
        NSFileHandle *file = [NSFileHandle fileHandleForWritingAtPath:logFilePath];
        if ([attrs fileSize] > truncateBytes) {
            [file truncateFileAtOffset:0];
            [file writeData:[NSData dataWithBytes:txtHeader length:3]];
        }

        [file seekToEndOfFile];
        [file writeData:logEntry];
        [file closeFile];
    }
}
@end