#import "MajascanPlugin.h"
#import <majascan_qr/majascan_qr-Swift.h>

@implementation MajascanPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMajascanPlugin registerWithRegistrar:registrar];
}
@end
